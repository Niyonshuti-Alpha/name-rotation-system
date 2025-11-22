// Load tasks on page load
window.addEventListener('DOMContentLoaded', () => {
    loadTasks();
});

// Generate tasks
async function handleGenerateTasks(event) {
    event.preventDefault();
    
    const numberOfNames = parseInt(document.getElementById('numberOfNames').value);
    
    if (numberOfNames < 4) {
        showToast('Minimum 4 names required for task generation', 'error');
        return;
    }
    
    if (!confirm(`Generate tasks for ${numberOfNames} names? This will clear existing tasks for today.`)) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/tasks/generate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify({ numberOfNames })
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            showToast('Tasks generated successfully!', 'success');
            loadTasks();
        } else {
            showToast('Failed to generate tasks: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('Generate tasks error:', error);
        showToast('Error generating tasks', 'error');
    }
}

// Load all tasks
async function loadTasks() {
    await Promise.all([
        loadNormalTasks(),
        loadSpecialTasks()
    ]);
}

// Load normal tasks
async function loadNormalTasks() {
    try {
        const response = await fetch(`${API_BASE}/tasks/normal`, {
            credentials: 'include'
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            displayNormalTasks(result.data);
        } else {
            showToast('Failed to load normal tasks', 'error');
        }
    } catch (error) {
        console.error('Load normal tasks error:', error);
        showToast('Error loading normal tasks', 'error');
    }
}

// Load special tasks
async function loadSpecialTasks() {
    try {
        const response = await fetch(`${API_BASE}/tasks/special`, {
            credentials: 'include'
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            displaySpecialTasks(result.data);
        } else {
            showToast('Failed to load special tasks', 'error');
        }
    } catch (error) {
        console.error('Load special tasks error:', error);
        showToast('Error loading special tasks', 'error');
    }
}

// Display normal tasks
function displayNormalTasks(tasks) {
    const container = document.getElementById('normalTasksContainer');
    
    if (!tasks || tasks.length === 0) {
        container.innerHTML = `
            <div class="empty-tasks">
                <div class="empty-tasks-icon">📝</div>
                <h3>No tasks generated yet</h3>
                <p>Click "Generate Tasks" to create task assignments</p>
            </div>
        `;
        return;
    }
    
    const tasksHTML = tasks.map((task, index) => `
        <div class="task-card">
            <div class="task-header">
                <div class="task-name">${index + 1}. ${task.name}</div>
                <span class="task-badge normal">Normal Task</span>
            </div>
            <div class="task-description">
                ${task.taskName || '<em style="color: #aaa;">Ijambo ry Imana</em>'}
            </div>
            <div class="task-actions">
                <button onclick="openReplaceModal(${task.id}, '${task.name}')" class="replace-btn">
                    ✏️ Replace Name
                </button>
            </div>
        </div>
    `).join('');
    
    container.innerHTML = tasksHTML;
}

// Display special tasks
function displaySpecialTasks(tasks) {
    const container = document.getElementById('specialTasksContainer');
    
    if (!tasks || tasks.length === 0) {
        container.innerHTML = `
            <div class="empty-tasks">
                <div class="empty-tasks-icon">⭐</div>
                <h3>No special tasks yet</h3>
                <p>Special tasks will appear here after generation</p>
            </div>
        `;
        return;
    }
    
    const tasksHTML = `
        <div class="tasks-grid">
            ${tasks.map((task, index) => `
                <div class="task-card special">
                    <div class="task-header">
                        <div class="task-name">${index + 1}. ${task.name}</div>
                        <span class="task-badge special">⭐ Special</span>
                    </div>
                    <div class="task-description">
                        Saturday and sunday intercession
                    </div>
                    <div class="task-actions">
                        <button onclick="openReplaceModal(${task.id}, '${task.name}')" class="replace-btn">
                            ✏️ Replace Name
                        </button>
                    </div>
                </div>
            `).join('')}
        </div>
    `;
    
    container.innerHTML = tasksHTML;
}

// Clear all tasks
async function handleClearTasks() {
    if (!confirm('Are you sure you want to clear all tasks for today? This cannot be undone.')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/tasks`, {
            method: 'DELETE',
            credentials: 'include'
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            showToast('All tasks cleared successfully!', 'success');
            loadTasks();
        } else {
            showToast('Failed to clear tasks: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('Clear tasks error:', error);
        showToast('Error clearing tasks', 'error');
    }
}

// NEW: Name replacement functionality
let currentReplacementTaskId = null;

function openReplaceModal(taskId, currentName) {
    currentReplacementTaskId = taskId;
    document.getElementById('replaceModal').classList.add('show');
    loadAvailableNames();
}

function closeReplaceModal() {
    document.getElementById('replaceModal').classList.remove('show');
    currentReplacementTaskId = null;
    document.getElementById('availableNames').innerHTML = '<option value="">Loading names...</option>';
}

async function loadAvailableNames() {
    try {
        const response = await fetch(`${API_BASE}/names/active`, {
            credentials: 'include'
        });
        const result = await response.json();
        
        if (result.status === 'success') {
            const select = document.getElementById('availableNames');
            select.innerHTML = '<option value="">Select a name...</option>' + 
                result.data.map(name => 
                    `<option value="${name.id}">${name.name}</option>`
                ).join('');
        } else {
            showToast('Failed to load available names', 'error');
        }
    } catch (error) {
        console.error('Load available names error:', error);
        showToast('Error loading available names', 'error');
    }
}

async function confirmReplace() {
    const newNameId = document.getElementById('availableNames').value;
    
    if (!newNameId) {
        showToast('Please select a name', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/tasks/${currentReplacementTaskId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ newNameId: parseInt(newNameId) })
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            showToast('Name replaced successfully!', 'success');
            closeReplaceModal();
            loadTasks(); // Reload to see changes
        } else {
            showToast('Failed to replace name: ' + result.message, 'error');
        }
    } catch (error) {
        console.error('Replace name error:', error);
        showToast('Error replacing name', 'error');
    }
}

// Close modal when clicking outside
document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('replaceModal');
    if (modal) {
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                closeReplaceModal();
            }
        });
    }
});