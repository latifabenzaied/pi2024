console.log('omar');

// document.get("createinputfile").addEventListener("change", function (event) {
//     console.log('omar');
//     var imageFile = this.files[0];
//     testImage(imageFile);
// })

$(document).ready(function () {

    $('#createinputfile').on('input', function() {
        console.log('omar');
        var imageFile = this.files[0];
        testImage(imageFile);
    });
    document.getElementById('name').addEventListener('input', validateName);
    document.getElementById('lastname').addEventListener('input', validateLastName);
});


let popup = document.getElementById("popup");

function openPopup() {
    popup.classList.add("open-popup")

}

function closePopup() {
    popup.classList.remove("open-popup");
    redirectToAnotherRoute();

}

function addPost(event) {
    let value = "../../marketPlaceImages/Spinner@1x-1.0s-200px-200px.gif"
    $('#addAbnBtn').html('<img src="' + value + '" width="30"/>')
    $('#addAbnBtn').addClass('btnTransparent');
    $('#addAbnBtn').prop('disabled', true)

    const selectedOption = document.querySelector('.nice-select ul li.selected');
    const type_text = selectedOption.textContent.trim();

    let formData = new FormData();
    let name = $('#name').val();
    let lastname = $('#lastname').val();
    let type = type_text;
    const isNameValid = validateName();
    const isLastNameValid = validateLastName();

        if (isNameValid && isLastNameValid) {

        formData.append('image', $('#createinputfile').prop('files')[0]);
        formData.append('name', name);
        formData.append('lastname', lastname);
        formData.append('type', type);
        $.ajax({
            url: '/addAbonnement',
            type: "POST",
            data: formData,
            async: true,
            processData: false,
            contentType: false,
            success: function (response) {
                $('#addAbnBtn').html('Submit Item')
                $('#addAbnBtn').removeClass('btnTransparent');
                $('#addAbnBtn').prop('disabled', false)
                showValidPop("Subscription Inserted")
                redirectToAnotherRoute();
            },
            error: function (response) {
                $('#addAbnBtn').html('Submit Item')
                $('#addAbnBtn').removeClass('btnTransparent');
                $('#addAbnBtn').prop('disabled', false)
                console.error("error");
                 showInvalidPop("Image not inserted !!!!")
            },
        });
    } else {
            $('#addAbnBtn').html('Submit Item')
            $('#addAbnBtn').removeClass('btnTransparent');
            $('#addAbnBtn').prop('disabled', false)
            showInvalidPop("fields are not written")
    }
}


function redirectToAnotherRoute() {
    // Wait for 3 seconds (3000 milliseconds)
    setTimeout(function () {
        // Redirect to another route
        window.location.href = '/showAbonnement'; // Replace '/your-new-route' with the actual route
    }, 2000); // 3000 milliseconds = 3 seconds
}


const nameRegex = /^[A-Za-z\s]+$/;
const lastNameRegex = /^[A-Za-z\s]+$/;

function validateName() {
    const nameInput = document.getElementById('name');
    const name = nameInput.value.trim();
    if (nameRegex.test(name)) {
        nameInput.style.borderColor = 'green';
        return true;
    } else {
        nameInput.style.borderColor = 'red';
        return false;
    }
}

function validateLastName() {
    const lastNameInput = document.getElementById('lastname');
    const lastName = lastNameInput.value.trim();
    if (lastNameRegex.test(lastName)) {
        lastNameInput.style.borderColor = 'green';
        return true;
    } else {
        lastNameInput.style.borderColor = 'red';
        return false;
    }
}// Add event listeners for input fields


function testImage(filePath) {
    document.getElementById('loadingLogo').style.display = 'block';
    document.getElementById('overlay').style.display = 'block';
    var critere = 0;
    let rep = "";
    let formData = new FormData();
    formData.append('file', filePath);

    $.ajax({

        url: '/AbonnementScan',
        type: "POST", // Change the request type to POST
        data: formData,

        processData: false,
        contentType: false,

        success: function (response) {

            for (i = 0; i < 10; i++) {
                if (response.result.tags[i].confidence > 0 && response.result.tags[i].tag.en == "person") {
                    critere++;
                    rep = rep + "person avec une confiance de " + response.result.tags[i].confidence + " \n";
                }
                if (response.result.tags[i].confidence > 30 , response.result.tags[i].tag.en == "portrait") {
                    critere++;
                    rep = rep + "Image portrait avec une confiance de " + response.result.tags[i].confidence + "  \n";

                }
            }
            console.log(response);
            console.log(rep);

            document.getElementById('loadingLogo').style.display = 'none';
            document.getElementById('overlay').style.display = 'none';

            if (critere == 2) {

                showValidPop("The  image respects our rules")


            } else {

                document.getElementById('loadingLogo').style.display = 'none';
                document.getElementById('overlay').style.display = 'none';
                rep = " ";
                showInvalidPop( " The rules have not been respected : \n The image sould be of an humain and in portrait mode  ")

            }
        },
        error: function (xhr, status, error) {
            document.getElementById('loadingLogo').style.display = 'none';
            document.getElementById('overlay').style.display = 'none';
            showInvalidPop("no image is inserted")        }
    });
}


// Add an event listener for when a file is selected

/*
var input = document.getElementById('createinputfile');
  input.addEventListener('change', function() {
    var fullPath = input.value;
    console.log(fullPath);
    testImage(fullPath);

  });*/

//$('#createinputfile').change(function (e) {
// Prints out the file path
//console.log(imagePath);
//  testImage("C:\Users\azeez\Downloads\images (3).jpg");
//});



  
  