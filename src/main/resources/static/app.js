let inventoryChartInstance = null;

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

    // Setup Admin View
    if (userRole === 'ROLE_ADMIN') {
        $('#userBuildsSection, #buildDetailsSection, #navMyBuildsBtn, #analyticsSection, #navCatalogueBtn').addClass('d-none');
        $('#inventorySection, #navAnalyticsBtn').removeClass('d-none');
    } 
    // Setup User View
    else {
        $('#inventorySection, #buildDetailsSection, #analyticsSection, #navAnalyticsBtn').addClass('d-none');
        $('#userBuildsSection, #navMyBuildsBtn').removeClass('d-none');

        if (typeof globalThis.loadBuildsList === 'function') {
            globalThis.loadBuildsList();
        }
    }

    if (typeof globalThis.loadInventory === 'function') {
        globalThis.loadInventory();
    }
};

globalThis.loadAnalytics = function() {
    $.ajax({
        url: '/api/analytics/stock',
        method: 'GET',
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') },
        success: function(data) {
            const canvas = document.getElementById('inventoryChart');
            const noDataMsg = document.getElementById('noDataMessage');

            // Handle empty database scenario
            if (!data || data.length === 0) {
                canvas.style.display = 'none';
                noDataMsg.classList.remove('d-none');
                return;
            }

            canvas.style.display = 'block';
            noDataMsg.classList.add('d-none');

            const labels = data.map(item => item.category);
            const stockValues = data.map(item => item.totalStock);

            // Destroy the old chart if it exists so they don't overlap
            if (inventoryChartInstance) {
                inventoryChartInstance.destroy();
            }

            // Draw the Pie Chart
            const ctx = canvas.getContext('2d');
            inventoryChartInstance = new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Total Stock',
                        data: stockValues,
                        backgroundColor: ['#c850c0', '#4158d0', '#ffcc70', '#36304a', '#198754', '#0dcaf0'],
                        borderWidth: 2,
                        borderColor: '#ffffff'
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { position: 'bottom' },
                        title: { display: true, text: 'Stock Levels by Category', font: { size: 16 } }
                    }
                }
            });
        }
    });
};

$(document).ready(function() {
    globalThis.checkAuthState();

    // Toggle to Analytics Page
    $('#navAnalyticsBtn').click(function() {
        $('#inventorySection').addClass('d-none');
        $('#analyticsSection, #navCatalogueBtn').removeClass('d-none');
        $(this).addClass('d-none');
        globalThis.loadAnalytics();
    });

    // Toggle back to Catalogue Page
    $('#navCatalogueBtn').click(function() {
        $('#analyticsSection, #buildDetailsSection').addClass('d-none');
        $('#inventorySection').removeClass('d-none');
        $(this).addClass('d-none');
        
        const userRole = localStorage.getItem('currentUser') === "admin" ? "ROLE_ADMIN" : "ROLE_USER";
        if (userRole === 'ROLE_ADMIN') {
            $('#navAnalyticsBtn').removeClass('d-none');
        } else {
            $('#navMyBuildsBtn').removeClass('d-none');
        }
    });
});