// ==========================================
// 1. JWT AUTHENTICATION & SETUP
// ==========================================
const token = localStorage.getItem('jwtToken');
const currentUser = localStorage.getItem('currentUser');

// If there's no token in memory, kick them back to the login page immediately
if (!token) {
    window.location.href = '/login.html';
}

// Automatically attach the JWT token to EVERY ajax request
$.ajaxSetup({
    headers: {
        'Authorization': 'Bearer ' + token
    }
});

// ==========================================
// 2. MAIN APPLICATION LOGIC
// ==========================================
$(document).ready(function () {
    // Relative URLs so it works on any port
	$.fn.dataTable.ext.errMode = 'none';
    const apiPartsUrl = '/api/parts';
    const apiBuildsUrl = '/api/builds';
    
    // Simple role check: If they logged in as "admin", give them admin rights.
    const userRole = currentUser === "admin" ? "ROLE_ADMIN" : "ROLE_USER"; 

    let partsTable;
    let deleteId = null;

    // --- LOGOUT LOGIC ---
    $('#logoutBtn').click(function(e) {
        e.preventDefault(); 
        // Wipe the browser memory and redirect
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('currentUser');
        window.location.href = '/login.html';
    });

    // --- NAVIGATION LOGIC ---
    $('#navInventory').click(function() {
        $('#inventorySection').show();
        $('#buildsSection').hide();
        $(this).addClass('active');
        $('#navBuilds').removeClass('active');
        partsTable.ajax.reload(); // Refresh data
    });

    $('#navBuilds').click(function() {
        $('#inventorySection').hide();
        $('#buildsSection').show();
        $(this).addClass('active');
        $('#navInventory').removeClass('active');
        loadBuilds();
    });

    // --- INVENTORY (DATATABLES) ---
    function initializePartsTable() {
        partsTable = $('#partsTable').DataTable({
            ajax: {
                url: apiPartsUrl,
                dataSrc: '' // Spring returns a raw List object
            },
            columns: [
                { data: 'name' },
                { data: 'manufacturer' },
                { data: 'category' },
                { 
                    data: 'price',
                    render: function(data) { return '€' + data.toFixed(2); }
                },
                { 
                    data: 'stockLevel',
                    render: function(data) {
                        if(data < 5) return `<span class="badge bg-danger">${data}</span>`;
                        return `<span class="badge bg-success">${data}</span>`;
                    }
                },
                {
                    data: null,
                    visible: (userRole === 'ROLE_ADMIN'), // Hide edit/delete for regular users
                    render: function (data) {
                        return `
                            <button class="btn btn-warning btn-sm edit-part-btn" data-id="${data.id}">
                                <i class="fa-solid fa-pen"></i>
                            </button>
                            <button class="btn btn-danger btn-sm delete-part-btn" data-id="${data.id}">
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        `;
                    }
                }
            ]
        });
    }

    initializePartsTable();

    // Show Add Modal
    $('#addPartBtn').click(function() {
        $('#partForm')[0].reset();
        $('#partId').val('');
        $('#partModalLabel').text('Add Hardware Part');
        $('#partModal').modal('show');
    });

    // Show Edit Modal
    $('#partsTable').on('click', '.edit-part-btn', function() {
        const id = $(this).data('id');
        $.get(`${apiPartsUrl}/${id}`, function(part) {
            $('#partId').val(part.id);
            $('#partName').val(part.name);
            $('#partManufacturer').val(part.manufacturer);
            $('#partCategory').val(part.category);
            $('#partPrice').val(part.price);
            $('#partStock').val(part.stockLevel);
            
            $('#partModalLabel').text('Edit Hardware Part');
            $('#partModal').modal('show');
        });
    });

    // Save Part (Create or Update)
    $('#savePartBtn').click(function() {
        const id = $('#partId').val();
        const partData = {
            name: $('#partName').val(),
            manufacturer: $('#partManufacturer').val(),
            category: $('#partCategory').val(),
            price: parseFloat($('#partPrice').val()),
            stockLevel: parseInt($('#partStock').val())
        };

        const method = id ? 'PUT' : 'POST';
        const url = id ? `${apiPartsUrl}/${id}` : apiPartsUrl;

        $.ajax({
            url: url,
            method: method,
            contentType: 'application/json',
            data: JSON.stringify(partData),
            success: function() {
                $('#partModal').modal('hide');
                partsTable.ajax.reload();
            },
            error: function(xhr) {
                alert("Error saving part: " + xhr.status);
            }
        });
    });

    // Delete Confirmation
    $('#partsTable').on('click', '.delete-part-btn', function() {
        deleteId = $(this).data('id');
        $('#deleteConfirmModal').modal('show');
    });

    $('#confirmDeleteBtn').click(function() {
        if(deleteId) {
            $.ajax({
                url: `${apiPartsUrl}/${deleteId}`,
                method: 'DELETE',
                success: function() {
                    $('#deleteConfirmModal').modal('hide');
                    partsTable.ajax.reload();
                }
            });
        }
    });

    // --- BUILDS LOGIC ---
    function loadBuilds() {
        $.get(`${apiBuildsUrl}/user/${currentUser}`, function(builds) {
            const container = $('#buildsContainer');
            container.empty();

            if(builds.length === 0) {
                container.html('<p class="text-muted">No builds found. Create one!</p>');
                return;
            }

            builds.forEach(build => {
                let partsHtml = '';
                build.parts.forEach(part => {
                    partsHtml += `<li>${part.name} - €${part.price}</li>`;
                });

                const cardHtml = `
                    <div class="col-md-4">
                        <div class="card mb-3 shadow-sm">
                            <div class="card-header bg-primary text-white d-flex justify-content-between">
                                <span>${build.buildName}</span>
                                <span>€${build.totalPrice.toFixed(2)}</span>
                            </div>
                            <div class="card-body">
                                <ul class="small">
                                    ${partsHtml || '<li>No parts added yet</li>'}
                                </ul>
                                <small class="text-muted">Created: ${new Date(build.createdAt).toLocaleDateString()}</small>
                            </div>
                        </div>
                    </div>
                `;
                container.append(cardHtml);
            });
        });
    }

    // Create New Build
    $('#createBuildBtn').click(function() {
        const name = $('#newBuildName').val();
        if(!name) return alert("Please enter a name");

        $.post(`${apiBuildsUrl}?username=${currentUser}&name=${name}`, function() {
            $('#newBuildName').val('');
            loadBuilds(); 
        }).fail(function() {
            alert("Error creating build");
        });
    });
});