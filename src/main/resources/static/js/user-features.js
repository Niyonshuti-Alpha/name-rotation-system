// User-specific functionality for new features

// Load all user features data
async function loadUserFeaturesData() {
    await Promise.all([
        loadMonthlyDesire(),
        loadUserAnnouncements(),
        loadUserIdeas(),
        loadDesires()
    ]);
}

// Monthly Desire functions
async function loadMonthlyDesire() {
    try {
        const response = await fetch(`${API_BASE}/monthly-desires/current`, { credentials: 'include' });
        const result = await response.json();
        
        const container = document.getElementById('monthlyDesireContainer');
        
        if (result.status === 'success' && result.data) {
            container.innerHTML = `
                <div class="monthly-desire-display">
                    <h2>📅 Monthly Desire</h2>
                    <div class="monthly-desire-content">
                        ${result.data.message}
                    </div>
                    <div class="monthly-desire-date">
                        Updated: ${formatDate(result.data.updatedAt)}
                    </div>
                </div>
            `;
        } else {
            container.innerHTML = `
                <div class="monthly-desire-display">
                    <h2>📅 Monthly Desire</h2>
                    <div class="monthly-desire-content">
                        No monthly desire has been set for this month.
                    </div>
                </div>
            `;
        }
    } catch (error) {
        console.error('Load monthly desire error:', error);
    }
}

// Announcements functions
async function loadUserAnnouncements() {
    try {
        const response = await fetch(`${API_BASE}/announcements/my-announcements`, { credentials: 'include' });
        const result = await response.json();
        
        const container = document.getElementById('userAnnouncementsContainer');
        
        if (result.status === 'success' && result.data.length > 0) {
            const announcementsHTML = result.data.slice(0, 5).map(announcement => `
                <div class="announcement-item ${announcement.sendTo.toLowerCase()}-users">
                    <strong>${announcement.title}</strong>
                    <p style="margin: 8px 0; font-size: 0.9em;">${announcement.content}</p>
                    <div style="display: flex; justify-content: space-between; align-items: center; font-size: 0.8em;">
                        <span>${formatDate(announcement.createdAt)}</span>
                        <span class="badge ${announcement.sendTo === 'ALL' ? 'badge-primary' : 'badge-success'}">
                            ${announcement.sendTo === 'ALL' ? 'To Everyone' : 'To You'}
                        </span>
                    </div>
                </div>
            `).join('');
            
            container.innerHTML = announcementsHTML;
        } else {
            container.innerHTML = '<p class="empty-tasks" style="padding: 20px;">No announcements for you</p>';
        }
    } catch (error) {
        console.error('Load announcements error:', error);
    }
}

function refreshAnnouncements() {
    loadUserAnnouncements();
    showToast('Announcements refreshed', 'success');
}

// Ideas functions
async function loadUserIdeas() {
    try {
        const response = await fetch(`${API_BASE}/ideas/my-ideas`, { credentials: 'include' });
        const result = await response.json();
        
        const container = document.getElementById('userIdeasList');
        
        if (result.status === 'success' && result.data.length > 0) {
            const ideasHTML = result.data.slice(0, 3).map(idea => `
                <div class="idea-item ${idea.status.toLowerCase()}" style="background: var(--light-color); padding: 10px; border-radius: 6px; margin-bottom: 8px; border-left: 3px solid ${getIdeaStatusColor(idea.status)};">
                    <strong>${idea.title}</strong>
                    <p style="margin: 5px 0; font-size: 0.85em;">${idea.content.substring(0, 80)}...</p>
                    <div style="display: flex; justify-content: space-between; align-items: center; font-size: 0.75em;">
                        <span>${formatDate(idea.createdAt)}</span>
                        <span class="badge badge-${getStatusBadgeColor(idea.status)}">${idea.status}</span>
                    </div>
                    ${idea.adminResponse ? `
                        <div style="margin-top: 8px; padding: 8px; background: #d4edda; border-radius: 4px; font-size: 0.8em;">
                            <strong>Admin Response:</strong> ${idea.adminResponse}
                        </div>
                    ` : ''}
                </div>
            `).join('');
            
            container.innerHTML = ideasHTML;
        } else {
            container.innerHTML = '<p class="empty-tasks" style="padding: 20px;">You haven\'t submitted any ideas yet</p>';
        }
    } catch (error) {
        console.error('Load user ideas error:', error);
    }
}

async function handleSubmitIdea(event) {
    event.preventDefault();
    
    const title = document.getElementById('ideaTitle').value.trim();
    const content = document.getElementById('ideaContent').value.trim();
    
    if (!title || !content) {
        showToast('Please fill in both title and content', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/ideas`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ title, content })
        });

        const result = await response.json();
        
        if (result.status === 'success') {
            showToast('Idea submitted successfully!', 'success');
            document.getElementById('ideaForm').reset();
            loadUserIdeas(); // Refresh the ideas list
        } else {
            showToast('Failed to submit idea: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('Submit idea error:', error);
        showToast('Error submitting idea', 'error');
    }
}

// Desires functions
async function loadDesires() {
    try {
        const [shortTermResponse, longTermResponse] = await Promise.all([
            fetch(`${API_BASE}/desires/short-term`, { credentials: 'include' }),
            fetch(`${API_BASE}/desires/long-term`, { credentials: 'include' })
        ]);

        const shortTermResult = await shortTermResponse.json();
        const longTermResult = await longTermResponse.json();
        
        // Display short-term desires
        const shortTermContainer = document.getElementById('userShortTermDesires');
        if (shortTermResult.status === 'success' && shortTermResult.data.length > 0) {
            shortTermContainer.innerHTML = shortTermResult.data.map(desire => `
                <div class="desire-item">
                    <p style="margin: 0; font-size: 0.9em;">${desire.description}</p>
                </div>
            `).join('');
        } else {
            shortTermContainer.innerHTML = '<p class="empty-tasks" style="padding: 10px;">No short-term desires</p>';
        }
        
        // Display long-term desires
        const longTermContainer = document.getElementById('userLongTermDesires');
        if (longTermResult.status === 'success' && longTermResult.data.length > 0) {
            longTermContainer.innerHTML = longTermResult.data.map(desire => `
                <div class="desire-item">
                    <p style="margin: 0; font-size: 0.9em;">${desire.description}</p>
                </div>
            `).join('');
        } else {
            longTermContainer.innerHTML = '<p class="empty-tasks" style="padding: 10px;">No long-term desires</p>';
        }
    } catch (error) {
        console.error('Load desires error:', error);
    }
}

// Utility functions
function getIdeaStatusColor(status) {
    switch(status) {
        case 'PENDING': return '#ffc107';
        case 'VIEWED': return '#17a2b8';
        case 'RESPONDED': return '#28a745';
        default: return '#6c757d';
    }
}

function getStatusBadgeColor(status) {
    switch(status) {
        case 'PENDING': return 'warning';
        case 'VIEWED': return 'info';
        case 'RESPONDED': return 'success';
        default: return 'secondary';
    }
}