'use strict';

var message = {},
    wrapper = {},
    buttonNewPhoto = {},
    buttonDownload = {},
    buttonDescribe = {},
    buttonRecommend = {},
    descriptionText = {},
    recommendationflag = {},
    video = {},
    canvas = {},
    styling = {},
    showing = {};
let hiddenContentDiv;

function initElement() {
    message = document.getElementById('msg');
    wrapper = document.getElementById('wrapper');
    buttonNewPhoto = document.getElementById('newphoto');
    buttonDownload = document.getElementById('download');
    buttonDescribe = document.getElementById('describe');
    //buttonRecommend = document.getElementById('recommend');
    descriptionText = document.getElementById('description');
    recommendationflag = document.getElementById('hiddendescriptionflag');
    video = document.querySelector('video');
    canvas = document.querySelector('canvas');
    styling = document.getElementById('hiddenstyleflag').value;
    showing = document.getElementById('hiddenshowflag').value;
    document.getElementById('hiddenstyleflag').value = 'OFF';
    document.getElementById('hiddenshowflag').value = 'OFF';
    hideSpinner();
    hiddenContentDiv = document.getElementById('hiddenContentDiv');

    if (descriptionText != '') {
        // buttonRecommend.removeAttribute('disabled');
        //recommendationflag.setAttribute('value', 'RECOMMEND');
        document.getElementById('hiddendescriptionflag').value = descriptionText;
        //alert(document.getElementById('hiddendescriptionflag').value);
    } else {
        // buttonRecommend.setAttribute('disabled', 'disabled');
        document.getElementById('hiddendescriptionflag').value = '';

    }

    if (navigator.mediaDevices === undefined) {
        navigator.mediaDevices = {};
    }

    if (navigator.mediaDevices.getUserMedia === undefined) {
        navigator.mediaDevices.getUserMedia = function (constraints) {

            var getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia;

            if (!getUserMedia) {
                return Promise.reject(new Error('getUserMedia is not implemented in this browser'));
            }

            return new Promise(function (resolve, reject) {
                getUserMedia.call(navigator, constraints, resolve, reject);
            })
        }
    }

    if (styling === 'ON') {
        showHiddenContent();
    }

    buttonDownload.addEventListener('click', function () {
        showHiddenContent();
    })

}

function showHiddenContent() {
    hiddenContentDiv.style.display = 'block';
}

function hideHiddenContent() {
    hiddenContentDiv.style.display = 'none';
}

function onTakeAPhoto() {
    canvas.getContext('2d').drawImage(video, 0, 0, video.width, video.height);
    buttonDownload.removeAttribute('disabled');
    document.getElementById('description').value = '';
    buttonDescribe.setAttribute('disabled', 'disabled');
    //buttonRecommend.setAttribute('disabled', 'disabled');
    document.getElementById('hiddenstyleflag').value = 'OFF';
    document.getElementById('hiddenshowflag').value = 'OFF';
    hideHiddenContent();
    imageoutputdiv.style.display = 'none';
    try {
        document.getElementById("imageoutputdiv").innerHTML = "";
        document.getElementById("imagerecommendationdiv").innerHTML = "";
    } catch (e) {
        console.error("Error handling exception:", e);
    }
}

function onDownloadPhoto() {
    canvas.toBlob(function (blob) {
        buttonDescribe.removeAttribute('disabled');
        /*var link = document.createElement('a');
    link.download = 'photo.jpg';
    link.setAttribute('href', URL.createObjectURL(blob));
    link.dispatchEvent(new MouseEvent('click'));
*/
        // Convert the Blob to a base64 encoded string

        var reader = new FileReader();
        reader.readAsDataURL(blob);
        reader.onloadend = function () {
            // Set the base64 encoded string to the hiddenimage input
            document.getElementById('hiddenimage').value = reader.result;
            //alert('Captured!');
            document.getElementById('hiddenstyleflag').value = 'ON';

            showHiddenContent();
            try {
                document.getElementById("imageoutputdiv").innerHTML = "";
                document.getElementById("imagerecommendationdiv").innerHTML = "";
            } catch (e) {
                console.error("Error handling exception:", e);
            }
        }

    }, 'image/jpeg', 1);

}

function showSpinner() {
    const spinner = document.getElementById('spinner');
    spinner.style.display = 'flex'; // Changed to flex to ensure alignment
    centerSpinner();
}

function hideSpinner() {
    document.getElementById('spinner').style.display = 'none';
}

function centerSpinner() {
    const spinner = document.getElementById('spinner');
    // Check if the spinner is visible before calculating position
    if (spinner.style.display === 'flex') {
        spinner.style.top = (window.innerHeight - spinner.offsetHeight) / 2 + 'px'; // Center vertically
        spinner.style.left = (window.innerWidth - spinner.offsetWidth) / 2 + 'px'; // Center horizontally
    }
}

function onDescribe() {
    try {
        document.getElementById("imageoutputdiv").innerHTML = "";
        document.getElementById("imagerecommendationdiv").innerHTML = "";
    } catch (e) {
        console.error("Error handling exception:", e);
    }
    document.getElementById('hiddendescriptionflag').value = document.getElementById('description').value;
    document.getElementById("description").value = "";
    document.getElementById("hiddendescriptionflag").value = "";
    document.getElementById('hiddenstyleflag').value = 'ON';
    document.getElementById('hiddenshowflag').value = 'OFF';
    showSpinner();
    //submit form
    document.getElementById('formprompt').submit();
}

function onUpload() {
    document.getElementById('hiddenimage').value = "";
    const file = document.getElementById('upload').files[0];
    if (file) {
        resizeImage(file, function (resizedDataUrl) {
            document.getElementById('hiddenimage').value = resizedDataUrl;
            console.log("Base64 Image Data:", document.getElementById('hiddenimage').value); // For debugging
            showHiddenContent();
            try {
                document.getElementById("imageoutputdiv").innerHTML = "";
                document.getElementById("imagerecommendationdiv").innerHTML = "";
            } catch (e) {
                console.error("Error handling exception:", e);
            }
        });

    } else {
        alert("Choose an image file");
    }
}
/*
 function onUpload() {
  const file = document.getElementById('upload').files[0];
  if (file) {
   // Check file size. Example 5MB limit, adjust as needed
   const maxSize = 5 * 1024 * 1024; // 5MB
   if (file.size > maxSize) {
    alert("File size exceeds 5MB. Please choose a smaller image.");
    return;
   }

   const reader = new FileReader();
   reader.readAsDataURL(file);
   reader.onloadend = function () {
    // Set the base64 encoded string to the hiddenimage input
    document.getElementById('hiddenimage').value = reader.result;
    console.log("Base64 Image Data:", document.getElementById('hiddenimage').value); // For debugging
    showHiddenContent();
   };
   reader.onerror = function(error) {
    alert("There was an issue reading your file.");
    console.error("FileReader error:", error);
   }
   try{
    document.getElementById("imageoutputdiv").innerHTML = "";
    document.getElementById("imagerecommendationdiv").innerHTML = "";
    }catch(e) {
      console.error("Error handling exception:", e);
     }

  } else {
   alert("Choose an image file");
  }
 }
 */

function validateDescription() {
    var desc = document.getElementById("description").value;
    if (desc == '' || desc == null) {
        //alert('Please enter a desired styling description.');
        return false; // Prevent form submission
    }
    return true; // Allow form submission if validation passes
}

function onGenerate() {
    //Clear any prior alerts if they are still on screen from a failed submission
    //document.getElementById('validation-alert').textContent = "";
    console.log("onGenerate called");
    console.log("Image Data:", document.getElementById('hiddenimage').value);
    console.log("Description Data:", document.getElementById('description').value);

    try {
        document.getElementById("imageoutputdiv").innerHTML = "";
        document.getElementById("imagerecommendationdiv").innerHTML = "";
    } catch (e) {
        console.error("Error handling exception:", e);
    }

    if (validateDescription()) {
        document.getElementById('hiddendescriptionflag').value = document.getElementById('description').value;
        document.getElementById('hiddenstyleflag').value = 'ON';
        document.getElementById('hiddenshowflag').value = 'ON';
        showSpinner();
        document.getElementById('formprompt').submit();

    } else {
        //Add error message to the page instead of an alert
        // document.getElementById('validation-alert').textContent = "Please enter a proper styling description.";
        document.getElementById('hiddendescriptionflag').value = document.getElementById('description').value;
        document.getElementById('hiddenstyleflag').value = 'ON';
        document.getElementById('hiddenshowflag').value = 'ON';
        return;
    }
}



function onLoadVideo() {
    /*  video.setAttribute('width', this.videoWidth);
     video.setAttribute('height', this.videoHeight);
     canvas.setAttribute('width', this.videoWidth);
     canvas.setAttribute('height', this.videoHeight); */
    video.setAttribute('width', 400);
    video.setAttribute('height', 400);
    canvas.setAttribute('width', 400);
    canvas.setAttribute('height', 400);

    video.play();
}

function onMediaStream(stream) {
    if ('srcObject' in video) {
        video.srcObject = stream;
    } else {
        video.src = window.URL.createObjectURL(stream);
    }

    message.style.display = 'none';
    wrapper.style.display = 'block';
    buttonNewPhoto.addEventListener('click', onTakeAPhoto);
    buttonDownload.addEventListener('click', onDownloadPhoto);
    video.addEventListener('loadedmetadata', onLoadVideo);



}

function onMediaError(err) {
    message.innerHTML = err.name + ': ' + err.message;
}

function initEvent() {
    navigator.mediaDevices
        .getUserMedia({
            video: true
        })
        .then(onMediaStream)
        .catch(onMediaError);
}

function flipImage(event) {
    const container = event.currentTarget;
    container.classList.toggle('flipped');
}

// New function to resize the image while ensuring that the size is less than 1MB
function resizeImage(file, callback) {
    const reader = new FileReader();
    reader.onload = function (event) {
        const img = new Image();
        img.onload = function () {
            let width = img.width;
            let height = img.height;

            const canvas = document.createElement('canvas');
            const ctx = canvas.getContext('2d');
            canvas.width = width;
            canvas.height = height;
            ctx.drawImage(img, 0, 0, width, height);

            let resizedDataUrl = canvas.toDataURL('image/jpeg', 0.9);

            // Calculate the size in bytes
            const byteSize = atob(resizedDataUrl.split(',')[1]).length;
            const sizeInMB = byteSize / (1024 * 1024)

            let quality = 0.9; // starting quality
            // Reduce quality until size is under 1MB
            while (sizeInMB > 1 && quality > 0.1) {
                quality -= 0.1;
                resizedDataUrl = canvas.toDataURL('image/jpeg', quality)
                const byteSize = atob(resizedDataUrl.split(',')[1]).length;
                const sizeInMB = byteSize / (1024 * 1024)
                console.log('Size', sizeInMB);

            }

            if (quality <= 0.1) {
                alert("The uploaded image couldn't be resized to under 1MB");
                document.getElementById('hiddenimage').value = "";
                document.getElementById('hiddendescriptionflag').value = "";
                document.getElementById('description').value = "";
                document.getElementById('hiddenstyleflag').value = "";
                document.getElementById('hiddenshowflag').value = "";
                document.getElementById('hiddenimageoutfit').value = "";



                hideHiddenContent();
                return;
            }

            callback(resizedDataUrl);
        };
        img.onerror = function () {
            alert("There was an issue loading your image, please try a different one.");
        }
        img.src = event.target.result;
    };
    reader.onerror = function () {
        alert("There was an issue reading your image, please try again.");
    }
    reader.readAsDataURL(file);
}

  function selectImage(event, container) {
    // Remove selection from the previous selected image if any
    const prevSelected = document.querySelector('#imagerecommendationsdiv .image-container.selected');
    if (prevSelected) {
        prevSelected.classList.remove('selected');
    }

   // Add "selected" class to container element.
    container.classList.add('selected');

    //var selectedImageSrc = container.querySelector('img').src;
    var selectedImageSrc = container.querySelector('p').textContent;
    console.log("Selected image source:", selectedImageSrc);

    // Store the selected image source
    document.getElementById('hiddenimageoutfit').value = selectedImageSrc;
    //alert(document.getElementById('hiddenimageoutfit').value);
}

  function init() {
      initElement();
      initEvent();
      // Recalculate position on window resize
      window.addEventListener('resize', centerSpinner);
  }

  



  if (window.location.protocol != 'https:' && window.location.protocol != 'file:') {
      window.location.href = 'https:' + window.location.href.substring(window.location.protocol.length);
  }

  window.addEventListener('DOMContentLoaded', init);