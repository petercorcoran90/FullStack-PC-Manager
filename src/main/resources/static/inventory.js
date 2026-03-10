let partsTable = null;
let deleteId = null;
const apiPartsUrl = '/api/parts';

function getAuthHeaders() {
    return { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') };
}

globalThis.loadInventory = function() {
    const currentUser = localStorage.getItem('currentUser');
    const userRole = currentUser === "admin" ? "ROLE_ADMIN" : "ROLE_USER";

    if (userRole === 'ROLE_ADMIN') {
        $('#addPartBtn').show();
    } else {
        $('#addPartBtn').hide();
    }

    if (partsTable) {
        partsTable.ajax.reload();
    } else {
        $.fn.dataTable.ext.errMode = 'none';
        partsTable = $('#partsTable').DataTable({
            ajax: {
                url: apiPartsUrl,
                dataSrc: '',
                headers: getAuthHeaders()
            },
            dom: '<"wrap-table100"rt><"d-flex justify-content-between mt-4 w-100"<"dt-floating-card d-flex align-items-center gap-3"li><"dt-floating-card d-flex align-items-center gap-3"fp>>',
            language: { search: "", searchPlaceholder: "Search parts..." },
            columns: [
                { data: 'name' },
                { data: 'manufacturer' },
                { data: 'category' },
                { data: 'price', render: function(data) { return '€' + (data || 0).toFixed(2); } },
                {
                    data: 'stockLevel', render: function(data) {
                        return data < 5 ? `<span class="badge bg-danger">${data}</span>` : `<span class="badge bg-success">${data}</span>`;
                    }
                },
                {
                    data: null,
                    orderable: false,
                    render: function(data) {
                        const currentRole = localStorage.getItem('currentUser') === "admin" ? "ROLE_ADMIN" : "ROLE_USER";

                        if (currentRole === 'ROLE_ADMIN') {
                            return `
                                <button class="btn btn-warning btn-sm edit-part-btn" data-id="${data.id}"><i class="fa-solid fa-pen"></i></button>
                                <button class="btn btn-danger btn-sm delete-part-btn" data-id="${data.id}"><i class="fa-solid fa-trash"></i></button>
                            `;
                        } else {
                            return `
                                <button class="btn btn-success btn-sm add-to-build-btn" data-id="${data.id}"><i class="fa-solid fa-plus"></i> Add</button>
                            `;
                        }
                    }
                }
            ]
        });
    }
};

$(document).ready(function() {
    $('#addPartBtn').click(function() {
        $('#partForm')[0].reset();
        $('#partId').val('');
        $('#modalErrorAlert').addClass('d-none');
        $('#partModalLabel').text('Add Hardware Part');
        $('#partModal').modal('show');
    });

    $('#partsTable').on('click', '.edit-part-btn', function() {
        const id = $(this).data('id');
        $.ajax({
            url: `${apiPartsUrl}/${id}`,
            method: 'GET',
            headers: getAuthHeaders(),
            success: function(part) {
                $('#partId').val(part.id);
                $('#partName').val(part.name);
                $('#partManufacturer').val(part.manufacturer);
                $('#partCategory').val(part.category);
                $('#partPrice').val(part.price);
                $('#partStock').val(part.stockLevel);

                $('#modalErrorAlert').addClass('d-none');
                $('#partModalLabel').text('Edit Hardware Part');
                $('#partModal').modal('show');
            }
        });
    });

    $('#savePartBtn').click(function() {
        const id = $('#partId').val();
        const partData = {
            name: $('#partName').val(),
            manufacturer: $('#partManufacturer').val(),
            category: $('#partCategory').val(),
            price: Number.parseFloat($('#partPrice').val()),
            stockLevel: Number.parseInt($('#partStock').val(), 10)
        };

        $.ajax({
            url: id ? `${apiPartsUrl}/${id}` : apiPartsUrl,
            method: id ? 'PUT' : 'POST',
            contentType: 'application/json',
            headers: getAuthHeaders(),
            data: JSON.stringify(partData),
            success: function() {
                $('#partModal').modal('hide');
                $('#modalErrorAlert').addClass('d-none');
                partsTable.ajax.reload();
            },
            error: function(xhr) {
                const errorMsg = xhr.responseJSON?.message || "Invalid input detected.";
                $('#modalErrorAlert').text(errorMsg).removeClass('d-none');
            }
        });
    });

    $('#partsTable').on('click', '.delete-part-btn', function() {
        deleteId = $(this).data('id');
        $('#deleteConfirmModal').modal('show');
    });

    $('#confirmDeleteBtn').click(function() {
        if (deleteId) {
            $.ajax({
                url: `${apiPartsUrl}/${deleteId}`,
                method: 'DELETE',
                headers: getAuthHeaders(),
                success: function() {
                    $('#deleteConfirmModal').modal('hide');
                    partsTable.ajax.reload();
                }
            });
        }
    });
});