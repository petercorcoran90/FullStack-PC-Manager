$(document).ready(function() {
    
    $('#loginForm').submit(function(e) {
        e.preventDefault();

        const loginData = {
            username: $('#username').val(),
            password: $('#password').val()
        };

        $('#loginBtn').text('Logging in...').prop('disabled', true);

        $.ajax({
            url: '/api/users/login',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(loginData),
            success: function(response) {
                $('#loginBtn').text('Login').prop('disabled', false);
                localStorage.setItem('jwtToken', response.token);
                localStorage.setItem('currentUser', loginData.username);
                window.checkAuthState(); 
            },
            error: function(xhr) {
                $('#loginBtn').text('Login').prop('disabled', false);
                $('#loginErrorMsg').removeClass('d-none');
            }
        });
    });

    $('#logoutBtn').click(function() {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('currentUser');
        window.checkAuthState(); 
    });
});