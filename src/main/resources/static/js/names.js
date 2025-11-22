// Load names on page load
window.addEventListener('DOMContentLoaded', () => {
    loadNames();
});

// Load all names
async function loadNames() {
    try {
        const response = await fetch(`${API_BASE}/names`, {
            credentials: 'include'
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            displayNames(result.data);
        } else {
            showToast('Failed to load names: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('Load names error:', error);
        showToast('Error loading names', 'error');
    }
}

// Display names in table
function displayNames(names) {
    const container = document.getElementById('namesTableContainer');
    
    if (!names || names.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📝</div>
                <h3>No names yet</h3>
                <p>Add your first name to get started!</p>
            </div>
        `;
        return;
    }
    
    const tableHTML = `
        <table class="names-table">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Name</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                ${names.map((name, index) => `
                    <tr>
                        <td>${index + 1}</td>
                        <td><strong>${name.name}</strong></td>
                        <td><span class="badge badge-success">Active</span></td>
                        <td>
                            <button onclick="openEditModal(${name.id}, '${name.name}')" class="btn btn-secondary btn-sm">✏️ Edit</button>
                            <button onclick="handleDeleteName(${name.id}, '${name.name}')" class="btn btn-danger btn-sm">🗑️ Delete</button>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    
    container.innerHTML = tableHTML;
}

// Add new name
async function handleAddName(event) {
    event.preventDefault();
    
    const nameInput = document.getElementById('newName');
    const name = nameInput.value.trim();
    
    if (!name) {
        showToast('Please enter a name', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/names`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify({ name })
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            showToast('Name added successfully!', 'success');
            nameInput.value = '';
            loadNames();
        } else {
            showToast('Failed to add name: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('Add name error:', error);
        showToast('Error adding name', 'error');
    }
}

// Open edit modal
function openEditModal(id, name) {
    document.getElementById('editNameId').value = id;
    document.getElementById('editNameInput').value = name;
    document.getElementById('editModal').classList.add('show');
}

// Close edit modal
function closeEditModal() {
    document.getElementById('editModal').classList.remove('show');
}

// Update name
async function handleUpdateName(event) {
    event.preventDefault();
    
    const id = document.getElementById('editNameId').value;
    const name = document.getElementById('editNameInput').value.trim();
    
    if (!name) {
        showToast('Please enter a name', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/names/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify({ name })
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            showToast('Name updated successfully!', 'success');
            closeEditModal();
            loadNames();
        } else {
            showToast('Failed to update name: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('Update name error:', error);
        showToast('Error updating name', 'error');
    }
}

// Delete name
async function handleDeleteName(id, name) {
    if (!confirm(`Are you sure you want to delete "${name}"?`)) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/names/${id}`, {
            method: 'DELETE',
            credentials: 'include'
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            showToast('Name deleted successfully!', 'success');
            loadNames();
        } else {
            showToast('Failed to delete name: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('Delete name error:', error);
        showToast('Error deleting name', 'error');
    }
}

// Close modal when clicking outside
document.getElementById('editModal').addEventListener('click', (e) => {
    if (e.target.id === 'editModal') {
        closeEditModal();
    }
});