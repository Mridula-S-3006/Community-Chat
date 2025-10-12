if (isAuthenticated() && (window.location.pathname === '/login.html' || window.location.pathname === '/signup.html')) {
    window.location.href = '/dashboard.html';
}

document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.querySelector('form[action*="login"]');
    const signupForm = document.querySelector('form[action*="signup"]');

    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    if (signupForm) {
        signupForm.addEventListener('submit', handleSignup);
    }

    const signupLink = document.querySelector('a[href*="signup"]');
    if (signupLink) {
        signupLink.addEventListener('click', (e) => {
            e.preventDefault();
            window.location.href = '/signup.html';
        });
    }

    const loginLink = document.querySelector('a[href*="login"]');
    if (loginLink) {
        loginLink.addEventListener('click', (e) => {
            e.preventDefault();
            window.location.href = '/login.html';
        });
    }
});

async function handleLogin(e) {
    e.preventDefault();

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;

    if (!username || !password) {
        showError('Please fill in all fields');
        return;
    }

    try {
        const response = await authAPI.login({ username, password });

        if (response.token) {
            setToken(response.token);
            setCurrentUser({
                id: response.userId,
                username: response.username,
                email: response.email
            });

            showSuccess('Login successful! Redirecting...');
            setTimeout(() => {
                window.location.href = '/dashboard.html';
            }, 1000);
        }
    } catch (error) {
        showError(error.message || 'Login failed. Please check your credentials.');
    }
}

async function handleSignup(e) {
    e.preventDefault();

    const username = document.getElementById('username').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password')?.value;

    if (!username || !email || !password) {
        showError('Please fill in all fields');
        return;
    }

    if (confirmPassword && password !== confirmPassword) {
        showError('Passwords do not match');
        return;
    }

    if (password.length < 6) {
        showError('Password must be at least 6 characters long');
        return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        showError('Please enter a valid email address');
        return;
    }

    try {
        const response = await authAPI.signup({ username, email, password });

        if (response.token) {
            setToken(response.token);
            setCurrentUser({
                id: response.userId,
                username: response.username,
                email: response.email
            });

            showSuccess('Signup successful! Redirecting...');
            setTimeout(() => {
                window.location.href = '/dashboard.html';
            }, 1000);
        } else {
            showSuccess('Signup successful! Please login.');
            setTimeout(() => {
                window.location.href = '/login.html';
            }, 1500);
        }
    } catch (error) {
        showError(error.message || 'Signup failed. Username or email may already exist.');
    }
}

function logout() {
    authAPI.logout();
}