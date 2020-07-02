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
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request)
    {
        long meetingDuration = request.getDuration();
        Collection<String> meetingAttendees = request.getAttendees();
        Collection<String> optionalAttendees = request.getOptionalAttendees();
        Collection<TimeRange> availableTime = new ArrayList<TimeRange>(); 
        Collection<TimeRange> removeList = new ArrayList<TimeRange>();
        Collection<TimeRange> addList = new ArrayList<TimeRange>();
        Collection<TimeRange> finalTimeList = Arrays.asList();
        if (meetingDuration > TimeRange.WHOLE_DAY.duration())
        {
            finalTimeList = Arrays.asList();
        }
        //If there are no meeting attendees or no events to look at 
        else if ((meetingAttendees.isEmpty()&&optionalAttendees.isEmpty()) || events.isEmpty())
        {
            finalTimeList = Arrays.asList(TimeRange.WHOLE_DAY);
        }
        else
        {
            for(Event event : events)
            {
                TimeRange whenEvent = event.getWhen();
                Set<String> eventAttendees = event.getAttendees();
                //Check if event attendees are invited to meeting OR there are no mandotory attendees/only optionals
                if (checkAttendees(eventAttendees,meetingAttendees)||
                    (meetingAttendees.isEmpty() && !optionalAttendees.isEmpty()))
                {
                    if (availableTime.isEmpty())
                    {
                        TimeRange frontTime1= TimeRange.fromStartEnd(TimeRange.START_OF_DAY,whenEvent.start(),false);
                        TimeRange backTime1 = TimeRange.fromStartEnd(whenEvent.end(),TimeRange.END_OF_DAY,true);
                        addFrontAndBackTimesToList(meetingDuration, frontTime1, backTime1, availableTime);
                    }
                    else
                    {
                        for (TimeRange openTime : availableTime)
                        {
                            //check if times overlap to produce the right time slots around the current event
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
                                    if (compareTimeRangeWithMeeting(meetingDuration, overlapTime.duration()))
                                    {
                                        addList.add(overlapTime);
                                    }
                                }
                            }
                        }    
                    }
                    availableTime.removeAll(removeList);
                    availableTime.addAll(addList);
                    finalTimeList = availableTime; 
                }  
                else if (optionalAttendees.isEmpty())
                {
                    
                    finalTimeList = Arrays.asList(TimeRange.WHOLE_DAY);
                } 
                //Checks if optional attendees can attend open times, but will not affect times made for mandatory attendees
                if (checkAttendees(eventAttendees, optionalAttendees) && !meetingAttendees.isEmpty())
                {
                    Collection<TimeRange> timeWithOptional = new ArrayList<TimeRange>(); 
                    int count = 0;
                    for(TimeRange currentOpenTime: availableTime)
                    {
                        if(!currentOpenTime.overlaps(whenEvent))
                        {
                            count++;
                            timeWithOptional.add(currentOpenTime);

                        }
                    }
                    if (count !=0)
                    {
                        finalTimeList = timeWithOptional;
                    }
                } 
            }
        }
        return finalTimeList;
    }

    /** Add open slot for potential meeting times before and after the event*/
    public void addFrontAndBackTimesToList(long meetingDuration, TimeRange frontTime,TimeRange backTime, Collection<TimeRange> timeRangeList)
    {
        if (compareTimeRangeWithMeeting(meetingDuration, frontTime.duration()))
        {
            timeRangeList.add(frontTime);
        }
        if (compareTimeRangeWithMeeting(meetingDuration, backTime.duration()))
        {
            timeRangeList.add(backTime);
            
        }
    }

    /** Check if event attendees are on the meeting or optional attendees list*/
    public boolean checkAttendees(Set<String> eventAttendees, Collection<String> checkAttendees)
    {
        for(String name: eventAttendees)
        {
            if (checkAttendees.contains(name))
            {
                return true;
            }
        }
        return false;
    }

    /** Compare a potential time slot to make sure the meeting duration can fit */
    public boolean compareTimeRangeWithMeeting(long meetingDuration, long openSlotTime)
    {
        return openSlotTime >= meetingDuration;
    }
}
