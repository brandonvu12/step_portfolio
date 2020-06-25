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

//Iterating through memes
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

//Parsing JSON comments
function getComments()
{
    var bruh = document.getElementById('nComment').value;
    var ending = "/data?nComment=" + bruh;
    console.log(ending);
    fetch(ending).then(response => response.json()).then((commentObj) => {
    var allComments = document.getElementById('allComments');
    while (allComments.hasChildNodes())
    {  
        allComments.removeChild(allComments.firstChild);
    }   
    commentObj.comments.forEach((line) => {
        allComments.appendChild(createListElement(line));
        });
    });
}

// Creates an <ul> element containing text
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

