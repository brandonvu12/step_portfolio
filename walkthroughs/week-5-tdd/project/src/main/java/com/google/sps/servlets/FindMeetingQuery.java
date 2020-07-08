// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/** Class that calculates open slot times given Event/Meeting Requests */
public final class FindMeetingQuery
{
    /** Reads a collection of events and people to find potential times for all of them to meet*/
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request)
    {
        long meetingDuration = request.getDuration();
        Collection<TimeRange> availableTime = new ArrayList<TimeRange>(); 
        if (meetingDuration > TimeRange.WHOLE_DAY.duration())
        {
            return Collections.emptyList();
        }
        //If there are no meeting attendees or no events to look at 
        else if ((request.getAttendees().isEmpty()&&request.getOptionalAttendees().isEmpty()) || events.isEmpty())
        {
            return Arrays.asList(TimeRange.WHOLE_DAY);
        }
        for(Event event : events)
        {
            TimeRange whenEvent = event.getWhen();
            Set<String> eventAttendees = event.getAttendees();
            //Check if event attendees are invited to meeting OR there are no mandotory attendees/only optionals
            if (checkAnyAttendeeInvited(eventAttendees,request.getAttendees())||
                (request.getAttendees().isEmpty() && !request.getOptionalAttendees().isEmpty()))
            {
                if (availableTime.isEmpty())
                {
                    addFirstOpenTimes(meetingDuration,availableTime,whenEvent);
                }
                else
                {
                    makeTimeAroundOverlapsInPotentialTimesWithEventTimes(availableTime, whenEvent, meetingDuration);   
                }
            }  
            else if (request.getOptionalAttendees().isEmpty())
            {
                return Arrays.asList(TimeRange.WHOLE_DAY);
            } 
            //Checks if optional attendees can attend open times, but will not affect times made for mandatory attendees
            if (checkAnyAttendeeInvited(eventAttendees, request.getOptionalAttendees()) && !request.getAttendees().isEmpty())
            {
                return timesForOptionalAttendees(availableTime,whenEvent);
            }
        }
        return availableTime;
    }

    private static void addFirstOpenTimes(long meetingDuration, Collection<TimeRange> availableTime, TimeRange whenEvent)
    {
        TimeRange frontTime1= TimeRange.fromStartEnd(TimeRange.START_OF_DAY,whenEvent.start(),false);
        TimeRange backTime1 = TimeRange.fromStartEnd(whenEvent.end(),TimeRange.END_OF_DAY,true);
        addFrontAndBackTimesToList(meetingDuration, frontTime1, backTime1, availableTime);
    }
    
    /** Checks if available times overlap event times to make available times around the event */
    private static void makeTimeAroundOverlapsInPotentialTimesWithEventTimes(Collection<TimeRange> availableTime,TimeRange whenEvent,
        long meetingDuration)
    {
        Collection<TimeRange> removeList = new ArrayList<TimeRange>();
        Collection<TimeRange> addList = new ArrayList<TimeRange>();
        for (TimeRange openTime : availableTime)
        {
            if(openTime.overlaps(whenEvent))
            {
                if(openTime.contains(whenEvent))
                {
                    removeList.add(openTime);
                    TimeRange frontTime2 = TimeRange.fromStartEnd(openTime.start(),whenEvent.start(),false);
                    TimeRange backTime2 = TimeRange.fromStartEnd(whenEvent.end(),TimeRange.END_OF_DAY,true);
                    addFrontAndBackTimesToList(meetingDuration, frontTime2, backTime2,addList);
                }
                else if (whenEvent.start() < openTime.start())
                {
                    removeList.add(openTime);
                    TimeRange overlapTime = TimeRange.fromStartEnd(whenEvent.end(),openTime.end(),false);
                    if (overlapTime.duration() >= meetingDuration)
                    {
                        addList.add(overlapTime);
                    }
                }
            }
        }
        availableTime.removeAll(removeList);
        availableTime.addAll(addList);    
    }

    private static Collection<TimeRange> timesForOptionalAttendees(Collection<TimeRange> availableTime,TimeRange whenEvent)
    {
        Collection<TimeRange> timeWithOptional = new ArrayList<TimeRange>(); 
        for(TimeRange currentOpenTime: availableTime)
        {
            if(!currentOpenTime.overlaps(whenEvent))
            {
                timeWithOptional.add(currentOpenTime);
            }
        }
        return timeWithOptional.isEmpty() ? availableTime : timeWithOptional;
    }
    /** Add open slot for potential meeting times before and after the event*/
    private static void addFrontAndBackTimesToList(long meetingDuration, TimeRange frontTime,TimeRange backTime, Collection<TimeRange> timeRangeList)
    {
        if (frontTime.duration() >= meetingDuration)
        {
            timeRangeList.add(frontTime);
        }
        if (backTime.duration() >= meetingDuration)
        {
            timeRangeList.add(backTime);     
        }
    }

    /** Check if at least one event attendee is on the meeting or optional attendees list*/
    private static boolean checkAnyAttendeeInvited(Set<String> eventAttendees, Collection<String> invitedAttendees)
    {
        for(String name: eventAttendees)
        {
            if (invitedAttendees.contains(name))
            {
                return true;
            }
        }
        return false;
    }
}
