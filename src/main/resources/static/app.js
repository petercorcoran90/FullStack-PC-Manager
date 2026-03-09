globalThis.checkAuthState = function() {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        $('#loginView').addClass('d-none');
        $('#dashboardView').removeClass('d-none');
        
        if (typeof globalThis.loadInventory === 'function') {
            globalThis.loadInventory();
        }
    } else {
        $('#dashboardView').addClass('d-none');
        $('#loginView').removeClass('d-none');
        $('#loginErrorMsg').addClass('d-none');
    }
};

$(document).ready(function() {
    globalThis.checkAuthState();
});