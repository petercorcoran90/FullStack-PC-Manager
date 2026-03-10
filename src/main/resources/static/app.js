globalThis.checkAuthState = function() {
    const token = localStorage.getItem('jwtToken');

    if (!token) {
        $('#dashboardView').addClass('d-none');
        $('#loginView').removeClass('d-none');
        $('#loginErrorMsg').addClass('d-none');
        return;
    }

    $('#loginView').addClass('d-none');
    $('#dashboardView').removeClass('d-none');

    const userRole = localStorage.getItem('currentUser') === "admin" ? "ROLE_ADMIN" : "ROLE_USER";

    if (userRole === 'ROLE_ADMIN') {
        $('#userBuildsSection, #buildDetailsSection, #navMyBuildsBtn').addClass('d-none');
        $('#inventorySection').removeClass('d-none');
    } else {
        $('#inventorySection, #buildDetailsSection').addClass('d-none');
        $('#userBuildsSection, #navMyBuildsBtn').removeClass('d-none');

        if (typeof globalThis.loadBuildsList === 'function') {
            globalThis.loadBuildsList();
        }
    }

    if (typeof globalThis.loadInventory === 'function') {
        globalThis.loadInventory();
    }
};

$(document).ready(function() {
    globalThis.checkAuthState();
});