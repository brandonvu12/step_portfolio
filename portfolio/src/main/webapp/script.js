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

/** Iterating through memes to display */
function addMeme()
{
  const imagesArray = 
  ['images/youtube_rewind.png','images/chrome_ram.gif', 'images/coffee.png','images/sundar.png'];
  const img = document.getElementById('meme-container');
  //checks for current image to change to the next one
    for (var i=0; i<imagesArray.length; i++)
    {
        if(img.src.match(imagesArray[i]))
        {
            if (i == (imagesArray.length)-1)
            {
                img.src = imagesArray[0];
            }
            else
            {
                img.src = imagesArray[i+1];
            }
            break;
        }
        else if (img.src == '')
        {
            img.src= imagesArray[0];
        }
    }
}

/**Create Map with styling and location */
function createMap() {
var location = {lat: 33.870350, lng: -117.924301};
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: location, zoom: 10,
       styles: [
            {elementType: 'geometry', stylers: [{color: '#242f3e'}]},
            {elementType: 'labels.text.stroke', stylers: [{color: '#242f3e'}]},
            {elementType: 'labels.text.fill', stylers: [{color: '#746855'}]},
            {
              featureType: 'administrative.locality',
              elementType: 'labels.text.fill',
              stylers: [{color: '#d59563'}]
            },
            {
              featureType: 'poi',
              elementType: 'labels.text.fill',
              stylers: [{color: '#d59563'}]
            },
            {
              featureType: 'poi.park',
              elementType: 'geometry',
              stylers: [{color: '#263c3f'}]
            },
            {
              featureType: 'poi.park',
              elementType: 'labels.text.fill',
              stylers: [{color: '#6b9a76'}]
            },
            {
              featureType: 'road',
              elementType: 'geometry',
              stylers: [{color: '#38414e'}]
            },
            {
              featureType: 'road',
              elementType: 'geometry.stroke',
              stylers: [{color: '#212a37'}]
            },
            {
              featureType: 'road',
              elementType: 'labels.text.fill',
              stylers: [{color: '#9ca5b3'}]
            },
            {
              featureType: 'road.highway',
              elementType: 'geometry',
              stylers: [{color: '#746855'}]
            },
            {
              featureType: 'road.highway',
              elementType: 'geometry.stroke',
              stylers: [{color: '#1f2835'}]
            },
            {
              featureType: 'road.highway',
              elementType: 'labels.text.fill',
              stylers: [{color: '#f3d19c'}]
            },
            {
              featureType: 'transit',
              elementType: 'geometry',
              stylers: [{color: '#2f3948'}]
            },
            {
              featureType: 'transit.station',
              elementType: 'labels.text.fill',
              stylers: [{color: '#d59563'}]
            },
            {
              featureType: 'water',
              elementType: 'geometry',
              stylers: [{color: '#17263c'}]
            },
            {
              featureType: 'water',
              elementType: 'labels.text.fill',
              stylers: [{color: '#515c6d'}]
            },
            {
              featureType: 'water',
              elementType: 'labels.text.stroke',
              stylers: [{color: '#17263c'}]
            }
          ]
      });
      var marker = new google.maps.Marker({position: location, map: map});
}

/** Call both functions when the website first loads */
function bothFunc()
{
    createMap();
    getComments();
    setLogInOutButton();
}

/**Parsing JSON comments*/
function getComments()
{
    var numComments = document.getElementById('inputNumComments').value;
    var ending = `/data?inputNumComments=${ numComments }`;
    fetch(ending).then(response => response.json()).then((commentObj) => {
    var allComments = document.getElementById('allComments');
    //removes all the current comments displayed and refresh with new ones
    allComments.innerHTML = '';
    addAllComments(commentObj);
    });
}

/** Check the login status to display the comments or force sign in */
function setLogInOutButton()
{
    var commentForm = document.getElementById('commentForm');
    fetch('/home').then(response => response.json()).then((login) => {
    if (login.loginStatus == 'true')
    {
        logButton(login.url, 'Logout', 'logoutButton');
        document.getElementById('loginButton').remove();
    }
    else (login.loginStatus == 'false')
    {
        hideComments();
        logButton(login.url, 'Login', 'loginButton');
    }
});
}

/** Create login/logout button depending on parameters */
function logButton(urlText, name, id)
{
   var btn = document.createElement('BUTTON');
    btn.innerHTML = name;
    document.getElementById('content').appendChild(btn);  
    btn.setAttribute('id', id);
    var log = document.getElementById(id);
    document.getElementById(id).onclick = function () {
        location.href = urlText; 
    };
}

/** Hide comment form and display message  */
function hideComments()
{
    var allComments = document.getElementById('commentForm');
    allComments.style.display = "none";
    var message = document.createElement('H3');
    message.innerText = 'Sign In To See Comments';
    document.getElementById('content').appendChild(message);
}

function addAllComments(commentObj)
{
    commentObj.comments.forEach((line) => {
        allComments.appendChild(createListElement(line));
        });
}

/** Creates an <ul> element containing text */
function createListElement(text)
{
    const ulElement = document.createElement('ul');
    if (text != "")
    {
        ulElement.innerText = text;
    }
    return ulElement;
}

function deleteAll()
{
    fetch('/delete-data', {method: 'POST'}).then(getComments());
}


