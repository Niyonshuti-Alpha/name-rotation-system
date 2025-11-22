// Load dashboard data on page load
window.addEventListener('DOMContentLoaded', () => {
    loadDashboardData();
});

// Load dashboard statistics
async function loadDashboardData() {
    try {
        const response = await fetch(`${API_BASE}/admin/dashboard`, {
            credentials: 'include'
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            const data = result.data;
            
            // Update statistics
            document.getElementById('totalNames').textContent = data.totalNames || 0;
            document.getElementById('activeNames').textContent = data.activeNames || 0;
            document.getElementById('tasksToday').textContent = data.tasksToday || 0;
            document.getElementById('specialTasks').textContent = data.specialTasks || 0;
            
            // Update system info
            document.getElementById('currentDate').textContent = formatDate(data.currentDate);
            document.getElementById('latestSession').textContent = data.latestSessionDate ? formatDate(data.latestSessionDate) : 'No sessions yet';
            document.getElementById('normalTasks').textContent = data.normalTasks || 0;
        } else {
            console.error('Failed to load dashboard:', result.message);
            showToast('Failed to load dashboard data', 'error');
        }
    } catch (error) {
        console.error('Dashboard error:', error);
        showToast('Error loading dashboard', 'error');
    }
}

// Refresh dashboard
function refreshDashboard() {
    showToast('Refreshing dashboard...', 'info');
    loadDashboardData();
}