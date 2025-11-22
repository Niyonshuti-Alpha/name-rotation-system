// Handle login form submission
async function handleLogin(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    const errorMessage = document.getElementById('errorMessage');
    const loginText = document.getElementById('loginText');
    const loginSpinner = document.getElementById('loginSpinner');
    const submitButton = event.target.querySelector('button[type="submit"]');
    
    // Clear previous errors
    errorMessage.textContent = '';
    errorMessage.classList.remove('show');
    
    // Validate inputs
    if (!username || !password) {
        errorMessage.textContent = 'Please enter both username and password';
        errorMessage.classList.add('show');
        return;
    }
    
    // Show loading state
    submitButton.disabled = true;
    loginText.style.display = 'none';
    loginSpinner.style.display = 'inline-block';
    
    try {
        const response = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify({ username, password })
        });
        
        const data = await response.json();
        
        if (data.status === 'success') {
            // Login successful - redirect to dashboard
            window.location.href = '/dashboard.html';
        } else {
            // Login failed - show error
            errorMessage.textContent = data.message || 'Login failed';
            errorMessage.classList.add('show');
            
            // Reset button state
            submitButton.disabled = false;
            loginText.style.display = 'inline';
            loginSpinner.style.display = 'none';
        }
    } catch (error) {
        console.error('Login error:', error);
        errorMessage.textContent = 'Connection error. Please try again.';
        errorMessage.classList.add('show');
        
        // Reset button state
        submitButton.disabled = false;
        loginText.style.display = 'inline';
        loginSpinner.style.display = 'none';
    }
}

// Check if already logged in
window.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch('http://localhost:8080/api/auth/check', {
            credentials: 'include'
        });
        
        const data = await response.json();
        
        if (data.status === 'success') {
            // Already logged in, redirect to dashboard
            window.location.href = '/dashboard.html';
        }
    } catch (error) {
        // Not logged in, stay on login page
        console.log('Not logged in');
    }
});