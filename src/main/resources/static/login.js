$(document).ready(function() {
    $('#loginForm').submit(function(e) {
        e.preventDefault();
        const loginData = {
            username: $('#username').val(),
            password: $('#password').val()
        };
        $.ajax({
            url: '/api/users/login',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(loginData),
            success: function(response) {
                localStorage.setItem('jwtToken', response.token);
                localStorage.setItem('currentUser', loginData.username);

                window.location.href = '/index.html';
            },
            error: function() {
                $('#errorMsg').show();
            }
        });
    });
});