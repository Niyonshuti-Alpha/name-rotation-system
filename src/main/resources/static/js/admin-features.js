// Admin-specific functionality for new features

// Load all admin features data
async function loadAdminFeaturesData() {
    await Promise.all([
        loadIdeasData(),
        loadMonthlyDesire(),
        loadAnnouncementsData(),
        loadUserActivityData(),
        loadDesiresData()
    ]);
}

// Ideas Management
async function loadIdeasData() {
    try {
        const [ideasResponse, countResponse] = await Promise.all([
            fetch(`${API_BASE}/ideas/latest`, { credentials: 'include' }),
            fetch(`${API_BASE}/ideas/pending/count`, { credentials: 'include' })
        ]);

        const ideasResult = await ideasResponse.json();
        const countResult = await countResponse.json();

        if (ideasResult.status === 'success') {
            displayLatestIdeas(ideasResult.data);
        }
        if (countResult.status === 'success') {
            document.getElementById('pendingIdeasCount').textContent = countResult.data;
            document.getElementById('totalIdeasCount').textContent = ideasResult.data?.length || 0;
        }
    } catch (error) {
        console.error('Load ideas error:', error);
    }
}

function displayLatestIdeas(ideas) {
    const container = document.getElementById('latestIdeasContainer');
    
    if (!ideas || ideas.length === 0) {
        container.innerHTML = '<p class="empty-tasks" style="padding: 20px;">No ideas submitted yet</p>';
        return;
    }

    const ideasHTML = ideas.slice(0, 3).map(idea => `
        <div class="idea-item ${idea.status.toLowerCase()}">
            <strong>${idea.title}</strong>
            <p style="margin: 5px 0; font-size: 0.9em;">${idea.content.substring(0, 100)}...</p>
            <div style="display: flex; justify-content: space-between; align-items: center; font-size: 0.8em;">
                <span>By: ${idea.username}</span>
                <span class="badge badge-${getStatusBadgeColor(idea.status)}">${idea.status}</span>
            </div>
            <div class="action-buttons">
                <button onclick="markIdeaAsViewed(${idea.id})" class="btn btn-info btn-xs" ${idea.status !== 'PENDING' ? 'disabled' : ''}>
                    Mark Viewed
                </button>
                <button onclick="openRespondModal(${idea.id})" class="btn btn-success btn-xs">
                    Respond
                </button>
            </div>
        </div>
    `).join('');

    container.innerHTML = ideasHTML;
}

async function markIdeaAsViewed(ideaId) {
    try {
        const response = await fetch(`${API_BASE}/ideas/${ideaId}/view`, {
            method: 'PUT',
            credentials: 'include'
        });

        const result = await response.json();
        
        if (result.status === 'success') {
            showToast('Idea marked as viewed', 'success');
            loadIdeasData();
        } else {
            showToast('Failed to mark idea: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('Mark idea error:', error);
        showToast('Error marking idea', 'error');
    }
}

function openRespondModal(ideaId) {
    // Store the idea ID for the response
    document.getElementById('respondIdeaId').value = ideaId;
    document.getElementById('respondModal').classList.add('show');
}

function closeRespondModal() {
    document.getElementById('respondModal').classList.remove('show');
    document.getElementById('adminResponse').value = '';
}

async function handleRespondToIdea(event) {
    event.preventDefault();
    
    const ideaId = document.getElementById('respondIdeaId').value;
    const response = document.getElementById('adminResponse').value.trim();
    
    if (!response) {
        showToast('Please enter a response', 'error');
        return;
    }
    
    try {
        const fetchResponse = await fetch(`${API_BASE}/ideas/${ideaId}/respond`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify(response)
        });

        const result = await fetchResponse.json();
        
        if (result.status === 'success') {
            showToast('Response sent successfully!', 'success');
            closeRespondModal();
            loadIdeasData();
        } else {
            showToast('Failed to respond: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('Respond to idea error:', error);
        showToast('Error responding to idea', 'error');
    }
}

function viewAllIdeas() {
    // In a full implementation, this would navigate to a dedicated ideas page
    showToast('Opening all ideas view...', 'info');
    // For now, just refresh the current data
    loadIdeasData();
}

// Monthly Desire Management
async function loadMonthlyDesire() {
    try {
        const response = await fetch(`${API_BASE}/monthly-desires/current`, { credentials: 'include' });
        const result = await response.json();
        
        const container = document.getElementById('monthlyDesireContainer');
        
        if (result.status === 'success' && result.data) {
            container.innerHTML = `
                <div style="background: #e8f4fd; padding: 15px; border-radius: 8px; border-left: 4px solid var(--info-color);">
                    <p style="margin: 0; line-height: 1.5;">${result.data.message}</p>
                    <div style="margin-top: 10px; font-size: 0.8em; color: #6c757d;">
                        Updated: ${formatDate(result.data.updatedAt)}
                    </div>
                </div>
            `;
        } else {
            container.innerHTML = '<p class="empty-tasks" style="padding: 20px;">No monthly desire set</p>';
        }
    } catch (error) {
        console.error('Load monthly desire error:', error);
    }
}

function openMonthlyDesireModal() {
    document.getElementById('monthlyDesireModal').classList.add('show');
}

function closeMonthlyDesireModal() {
    document.getElementById('monthlyDesireModal').classList.remove('show');
}

async function handleUpdateMonthlyDesire(event) {
    event.preventDefault();
    const message = document.getElementById('monthlyDesireMessage').value;
    
    try {
        const response = await fetch(`${API_BASE}/monthly-desires`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ message })
        });

        const result = await response.json();
        
        if (result.status === 'success') {
            showToast('Monthly desire updated successfully!', 'success');
            closeMonthlyDesireModal();
            loadMonthlyDesire();
        } else {
            showToast('Failed to update monthly desire: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('Update monthly desire error:', error);
        showToast('Error updating monthly desire', 'error');
    }
}

// Announcements Management
async function loadAnnouncementsData() {
    try {
        const [announcementsResponse, countResponse] = await Promise.all([
            fetch(`${API_BASE}/announcements`, { credentials: 'include' }),
            fetch(`${API_BASE}/announcements/active/count`, { credentials: 'include' })
        ]);

        const announcementsResult = await announcementsResponse.json();
        const countResult = await countResponse.json();

        if (announcementsResult.status === 'success') {
            displayLatestAnnouncements(announcementsResult.data);
        }
        if (countResult.status === 'success') {
            document.getElementById('activeAnnouncementsCount').textContent = countResult.data;
        }
    } catch (error) {
        console.error('Load announcements error:', error);
    }
}

function displayLatestAnnouncements(announcements) {
    const container = document.getElementById('latestAnnouncementsContainer');
    
    if (!announcements || announcements.length === 0) {
        container.innerHTML = '<p class="empty-tasks" style="padding: 20px;">No announcements created yet</p>';
        return;
    }

    const announcementsHTML = announcements.slice(0, 3).map(announcement => `
        <div class="announcement-item">
            <strong>${announcement.title}</strong>
            <p style="margin: 8px 0; font-size: 0.9em;">${announcement.content.substring(0, 80)}...</p>
            <div style="display: flex; justify-content: space-between; align-items: center; font-size: 0.8em;">
                <span>${formatDate(announcement.createdAt)}</span>
                <span class="badge ${announcement.sendTo === 'ALL' ? 'badge-primary' : 'badge-success'}">
                    ${announcement.sendTo === 'ALL' ? 'To All' : 'To User'}
                </span>
            </div>
            <div class="action-buttons">
                <button onclick="deleteAnnouncement(${announcement.id})" class="btn btn-danger btn-xs">
                    Delete
                </button>
            </div>
        </div>
    `).join('');

    container.innerHTML = announcementsHTML;
}

async function deleteAnnouncement(announcementId) {
    if (!confirm('Are you sure you want to delete this announcement?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/announcements/${announcementId}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        const result = await response.json();
        
        if (result.status === 'success') {
            showToast('Announcement deleted successfully', 'success');
            loadAnnouncementsData();
        } else {
            showToast('Failed to delete announcement: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('Delete announcement error:', error);
        showToast('Error deleting announcement', 'error');
    }
}

// UPDATED: Announcement Modal Functions with User ID implementation
let activeUsers = [];

function openAnnouncementModal() {
    document.getElementById('announcementModal').classList.add('show');
    loadActiveUsers();
}

function closeAnnouncementModal() {
    document.getElementById('announcementModal').classList.remove('show');
    resetAnnouncementForm();
}

function resetAnnouncementForm() {
    document.getElementById('announcementForm').reset();
    document.getElementById('userSelectionContainer').style.display = 'none';
    document.querySelector('input[name="sendTo"][value="ALL"]').checked = true;
}

function toggleUserSelection() {
    const userSelectionContainer = document.getElementById('userSelectionContainer');
    const specificUserRadio = document.querySelector('input[name="sendTo"][value="SPECIFIC"]');
    
    userSelectionContainer.style.display = specificUserRadio.checked ? 'block' : 'none';
    
    // If specific user is selected and no users loaded yet, load them
    if (specificUserRadio.checked && activeUsers.length === 0) {
        loadActiveUsers();
    }
}

// FIXED: Load active users for dropdown - No more confusing errors
async function loadActiveUsers() {
    try {
        const response = await fetch(`${API_BASE}/users/active`, {
            credentials: 'include'
        });
        
        const result = await response.json();
        
        // If we get data, assume it's successful regardless of message
        if (result.data && Array.isArray(result.data)) {
            activeUsers = result.data;
            populateUserDropdown();
        }
        // No else block - silently ignore if no users or other non-error responses
    } catch (error) {
        console.error('Load users error:', error);
        // Only show actual network errors
        if (error.message.includes('Network') || error.message.includes('Failed to fetch')) {
            showToast('Network error loading users', 'error');
        }
    }
}

// Populate user dropdown with usernames
function populateUserDropdown() {
    const userSelect = document.getElementById('specificUser');
    if (!userSelect) return;
    
    userSelect.innerHTML = '<option value="">Select a user...</option>';
    
    activeUsers.forEach(user => {
        const option = document.createElement('option');
        option.value = user.id; // Store user ID as value
        option.textContent = user.username; // Show username in dropdown
        userSelect.appendChild(option);
    });
}

// FIXED: Handle announcement creation with proper success check
async function handleCreateAnnouncement(event) {
    event.preventDefault();
    
    const title = document.getElementById('announcementTitle').value.trim();
    const content = document.getElementById('announcementContent').value.trim();
    const sendTo = document.querySelector('input[name="sendTo"]:checked').value;
    const specificUserId = sendTo === 'SPECIFIC' ? document.getElementById('specificUser').value : null;
    
    // Validation
    if (!title) {
        showToast('Please enter announcement title', 'error');
        return;
    }
    
    if (!content) {
        showToast('Please enter announcement message', 'error');
        return;
    }
    
    if (sendTo === 'SPECIFIC' && !specificUserId) {
        showToast('Please select a user for specific announcement', 'error');
        return;
    }
    
    try {
        const requestData = {
            title: title,
            content: content,
            sendTo: sendTo,
            specificUserId: specificUserId ? parseInt(specificUserId) : null
        };
        
        const response = await fetch(`${API_BASE}/announcements`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify(requestData)
        });
        
        const result = await response.json();
        
        // FIX: Check for both possible success indicators
        if (result.status === 'success' || result.success === true) {
            showToast('Announcement created successfully!', 'success');
            closeAnnouncementModal();
            loadAnnouncementsData(); // Refresh the announcements list
        } else {
            // If neither success indicator is true, show error
            const errorMsg = result.message || 'Unknown error occurred';
            showToast('Failed to create announcement: ' + errorMsg, 'error');
        }
    } catch (error) {
        console.error('Create announcement error:', error);
        showToast('Error creating announcement', 'error');
    }
}

// User Activity Management
async function loadUserActivityData() {
    try {
        const [statsResponse, topVisitorsResponse, inactiveResponse] = await Promise.all([
            fetch(`${API_BASE}/user-activity/statistics`, { credentials: 'include' }),
            fetch(`${API_BASE}/user-activity/top-visitors`, { credentials: 'include' }),
            fetch(`${API_BASE}/user-activity/inactive?days=7`, { credentials: 'include' })
        ]);

        const statsResult = await statsResponse.json();
        const topVisitorsResult = await topVisitorsResponse.json();
        const inactiveResult = await inactiveResponse.json();

        if (statsResult.status === 'success') {
            document.getElementById('totalVisits').textContent = statsResult.data.totalVisits;
        }
        if (inactiveResult.status === 'success') {
            document.getElementById('inactiveUsers').textContent = inactiveResult.data.length;
        }
        if (topVisitorsResult.status === 'success') {
            displayTopVisitors(topVisitorsResult.data);
        }
    } catch (error) {
        console.error('Load user activity error:', error);
    }
}

function displayTopVisitors(visitors) {
    const container = document.getElementById('topVisitorsContainer');
    
    if (!visitors || visitors.length === 0) {
        container.innerHTML = '<p class="empty-tasks" style="padding: 20px;">No user activity data</p>';
        return;
    }

    const visitorsHTML = visitors.slice(0, 3).map(visitor => `
        <div style="display: flex; justify-content: space-between; align-items: center; padding: 8px; background: var(--light-color); border-radius: 6px; margin-bottom: 5px;">
            <span>${visitor.username}</span>
            <span class="badge badge-primary">${visitor.visitCount} visits</span>
        </div>
    `).join('');

    container.innerHTML = visitorsHTML;
}

function viewUserActivity() {
    showToast('Opening user activity details...', 'info');
    // In full implementation, this would open a detailed view
}

// Desires Management
async function loadDesiresData() {
    try {
        const [shortTermResponse, longTermResponse] = await Promise.all([
            fetch(`${API_BASE}/desires/short-term`, { credentials: 'include' }),
            fetch(`${API_BASE}/desires/long-term`, { credentials: 'include' })
        ]);

        const shortTermResult = await shortTermResponse.json();
        const longTermResult = await longTermResponse.json();
        
        displayDesires(shortTermResult.data, 'shortTermDesiresContainer');
        displayDesires(longTermResult.data, 'longTermDesiresContainer');
    } catch (error) {
        console.error('Load desires error:', error);
    }
}

function displayDesires(desires, containerId) {
    const container = document.getElementById(containerId);
    
    if (!desires || desires.length === 0) {
        container.innerHTML = '<p class="empty-tasks" style="padding: 20px;">No desires added yet</p>';
        return;
    }

    const desiresHTML = desires.map(desire => `
        <div class="desire-item">
            <p style="margin: 0 0 10px 0;">${desire.description}</p>
            <div class="action-buttons">
                <button onclick="openEditDesireModal(${desire.id}, '${desire.description.replace(/'/g, "\\'")}', '${desire.category}')" class="btn btn-secondary btn-xs">
                    Edit
                </button>
                <button onclick="deleteDesire(${desire.id})" class="btn btn-danger btn-xs">
                    Delete
                </button>
            </div>
        </div>
    `).join('');

    container.innerHTML = desiresHTML;
}

function openDesireModal() {
    document.getElementById('desireModal').classList.add('show');
}

function closeDesireModal() {
    document.getElementById('desireModal').classList.remove('show');
    document.getElementById('desireForm').reset();
}

async function handleCreateDesire(event) {
    event.preventDefault();
    
    const description = document.getElementById('desireDescription').value.trim();
    const category = document.getElementById('desireCategory').value;
    
    if (!description || !category) {
        showToast('Please fill in all fields', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/desires`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ description, category })
        });

        const result = await response.json();
        
        if (result.status === 'success') {
            showToast('Desire created successfully!', 'success');
            closeDesireModal();
            loadDesiresData();
        } else {
            showToast('Failed to create desire: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('Create desire error:', error);
        showToast('Error creating desire', 'error');
    }
}

// FIXED: openEditDesireModal - No more GET request causing 405 error
function openEditDesireModal(desireId, currentDescription, currentCategory) {
    try {
        // Set the current values directly without fetching from API
        document.getElementById('editDesireId').value = desireId;
        document.getElementById('editDesireDescription').value = currentDescription;
        document.getElementById('editDesireCategory').value = currentCategory;
        document.getElementById('editDesireModal').classList.add('show');
    } catch (error) {
        console.error('Error opening edit modal:', error);
        showToast('Error opening edit form', 'error');
    }
}

function closeEditDesireModal() {
    document.getElementById('editDesireModal').classList.remove('show');
}

async function handleUpdateDesire(event) {
    event.preventDefault();
    
    const desireId = document.getElementById('editDesireId').value;
    const description = document.getElementById('editDesireDescription').value.trim();
    const category = document.getElementById('editDesireCategory').value;
    
    if (!description || !category) {
        showToast('Please fill in all fields', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/desires/${desireId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ description, category })
        });

        const result = await response.json();
        
        if (result.status === 'success') {
            showToast('Desire updated successfully!', 'success');
            closeEditDesireModal();
            loadDesiresData();
        } else {
            showToast('Failed to update desire: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('Update desire error:', error);
        showToast('Error updating desire', 'error');
    }
}

async function deleteDesire(desireId) {
    if (!confirm('Are you sure you want to delete this desire?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/desires/${desireId}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        const result = await response.json();
        
        if (result.status === 'success') {
            showToast('Desire deleted successfully', 'success');
            loadDesiresData();
        } else {
            showToast('Failed to delete desire: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('Delete desire error:', error);
        showToast('Error deleting desire', 'error');
    }
}

// Utility function
function getStatusBadgeColor(status) {
    switch(status) {
        case 'PENDING': return 'warning';
        case 'VIEWED': return 'info';
        case 'RESPONDED': return 'success';
        default: return 'secondary';
    }
}