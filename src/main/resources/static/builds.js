let activeBuildId = null;
let buildToDeleteId = null;
globalThis.loadBuildsList = function() {
    $('#userBuildsSection').removeClass('d-none');
    $('#buildDetailsSection, #inventorySection').addClass('d-none');
    activeBuildId = null;

    const username = localStorage.getItem('currentUser');
    $.ajax({
        url: `/api/builds/user/${username}`,
        method: 'GET',
        headers: getAuthHeaders(),
        success: function(builds) {
            const tbody = $('#buildsTableBody');
            tbody.empty();

            if (!builds || builds.length === 0) {
                tbody.append('<tr><td colspan="4" class="text-center text-muted py-4">No PC builds found. Click "New Build" to start!</td></tr>');
                return;
            }

            builds.forEach(b => {
                let dateStr = 'Unknown';
                if (b.createdAt) {
                    if (Array.isArray(b.createdAt)) {
                        dateStr = new Date(b.createdAt[0], b.createdAt[1] - 1, b.createdAt[2]).toLocaleDateString();
                    } else {
                        const d = new Date(b.createdAt);
                        dateStr = Number.isNaN(d.getTime()) ? 'Unknown' : d.toLocaleDateString();
                    }
                }

                const total = b.totalPrice || 0;

                tbody.append(`
				                    <tr>
				                        <td class="fw-bold" style="color: #4d4d4d;">${b.buildName}</td>
				                        <td>${dateStr}</td>
				                        <td class="fw-bold">€${total.toFixed(2)}</td>
				                        <td class="text-center">
				                            <button class="btn btn-warning btn-sm view-build-btn" data-id="${b.id}">
				                                <i class="fa-solid fa-pen"></i>
				                            </button>
				                            <button class="btn btn-danger btn-sm delete-build-btn" data-id="${b.id}">
				                                <i class="fa-solid fa-trash"></i>
				                            </button>
				                        </td>
				                    </tr>
				                `);
            });
        },
        error: function(err) {
            $('#buildsTableBody').html('<tr><td colspan="4" class="text-center text-danger py-4">Failed to load builds.</td></tr>');
        }
    });
};

globalThis.openBuildDetails = function(buildId) {
    activeBuildId = buildId;
    $('#userBuildsSection').addClass('d-none');
    $('#buildDetailsSection, #inventorySection').removeClass('d-none');

    if ($.fn.DataTable.isDataTable('#partsTable')) {
        $('#partsTable').DataTable().columns.adjust().draw();
    }

    refreshActiveBuild();
};

function refreshActiveBuild() {
    const username = localStorage.getItem('currentUser');
    $.ajax({
        url: `/api/builds/user/${username}`,
        method: 'GET',
        headers: getAuthHeaders(),
        success: function(builds) {
            const build = builds.find(b => b.id == activeBuildId);
            if (build) renderActiveBuild(build);
        }
    });
}

function renderActiveBuild(build) {
    $('#detailBuildName').text(build.buildName);
    const total = build.totalPrice || 0;
    $('#detailBuildTotal').text(total.toFixed(2));

    const partsList = $('#detailPartsList');
    partsList.empty();

    if (!build.parts || build.parts.length === 0) {
        partsList.append('<li class="list-group-item text-muted text-center py-4 bg-transparent border-0">No components added yet. Browse the catalogue below!</li>');
        return;
    }

    const groupedParts = {};
    build.parts.forEach(p => {
        if (groupedParts[p.id]) {
            groupedParts[p.id].quantity++;
        } else {
            groupedParts[p.id] = { ...p, quantity: 1 };
        }
    });

    Object.values(groupedParts).forEach(p => {
        const price = p.price || 0;
        const itemTotal = (price * p.quantity).toFixed(2);
        const quantityBadge = p.quantity > 1 ? `<span class="badge bg-secondary rounded-pill ms-2">x${p.quantity}</span>` : '';

        partsList.append(`
            <li class="list-group-item d-flex justify-content-between align-items-center bg-transparent px-0 border-bottom">
                <div>
                    <h6 class="mb-0 fw-bold text-dark">${p.name} ${quantityBadge}</h6>
                    <small class="text-muted">${p.category} | ${p.manufacturer} | €${price.toFixed(2)} each</small>
                </div>
                <div class="d-flex align-items-center">
                    <span class="fw-bold me-3 text-dark">€${itemTotal}</span>
                    <button class="btn btn-outline-danger btn-sm remove-part-btn rounded" data-id="${p.id}" title="Remove 1 unit">
                        <i class="fa-solid fa-minus"></i>
                    </button>
                </div>
            </li>
        `);
    });
}

$(document).ready(function() {

    $('#navMyBuildsBtn').click(function() {
        globalThis.loadBuildsList();
    });

    $(document).on('click', '.view-build-btn', function() {
        openBuildDetails($(this).data('id'));
    });

    $('#openCreateBuildBtn').click(function() {
        $('#newBuildName').val('');
        $('#createBuildModal').modal('show');
    });

    $('#createBuildForm').submit(function(e) {
        e.preventDefault();
        const name = $('#newBuildName').val();
        const username = localStorage.getItem('currentUser');
        $('#saveBuildBtn').text('Creating...').prop('disabled', true);

        $.ajax({
            url: `/api/builds?username=${username}&name=${encodeURIComponent(name)}`,
            method: 'POST',
            headers: getAuthHeaders(),
            success: function(newBuild) {
                $('#saveBuildBtn').text('Create Build').prop('disabled', false);
                $('#createBuildModal').modal('hide');
                openBuildDetails(newBuild.id);
            }
        });
    });

    $(document).on('click', '.add-to-build-btn', function() {
        if (!activeBuildId) return;

        const partId = $(this).data('id');
        const btn = $(this);
        btn.html('<i class="fa-solid fa-spinner fa-spin"></i>').prop('disabled', true);

        $.ajax({
            url: `/api/builds/${activeBuildId}/parts/${partId}`,
            method: 'POST',
            headers: getAuthHeaders(),
            success: function(updatedBuild) {
                btn.html('<i class="fa-solid fa-check"></i> Added').removeClass('btn-success').addClass('btn-primary');
                setTimeout(() => btn.html('<i class="fa-solid fa-plus"></i> Add').removeClass('btn-primary').addClass('btn-success').prop('disabled', false), 1500);
                renderActiveBuild(updatedBuild);
            }
        });
    });

    $(document).on('click', '.remove-part-btn', function() {
        const partId = $(this).data('id');
        const btn = $(this);
        btn.html('<i class="fa-solid fa-spinner fa-spin"></i>').prop('disabled', true);

        $.ajax({
            url: `/api/builds/${activeBuildId}/parts/${partId}`,
            method: 'DELETE',
            headers: getAuthHeaders(),
            success: function(updatedBuild) {
                renderActiveBuild(updatedBuild);
            }
        });
    });

    $(document).on('click', '.delete-build-btn', function(e) {
        e.stopPropagation();
        buildToDeleteId = $(this).data('id');
        $('#deleteBuildConfirmModal').modal('show');
    });

    $(document).on('click', '#confirmDeleteBuildBtn', function() {
        if (!buildToDeleteId) return;

        const modalBtn = $(this);
        modalBtn.prop('disabled', true).html('<i class="fa-solid fa-spinner fa-spin"></i>');

        $.ajax({
            url: `/api/builds/${buildToDeleteId}`,
            method: 'DELETE',
            headers: getAuthHeaders(),
            success: function() {
                $('#deleteBuildConfirmModal').modal('hide');
                modalBtn.prop('disabled', false).text('Delete');
                buildToDeleteId = null;
                globalThis.loadBuildsList();
            },
            error: function() {
                alert("Failed to delete the build. Please try again.");
                modalBtn.prop('disabled', false).text('Delete');
            }
        });
    });
});