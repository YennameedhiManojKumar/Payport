const TOKEN_KEY = 'payport_token';
const USER_KEY = 'payport_user';

function saveAuth(token, mobileNumber, name, upiId) {
    sessionStorage.setItem(TOKEN_KEY, token);
    sessionStorage.setItem(USER_KEY, JSON.stringify({ mobileNumber, name, upiId }));
}

function getToken() {
    return sessionStorage.getItem(TOKEN_KEY);
}

function getUser() {
    const user = sessionStorage.getItem(USER_KEY);
    return user ? JSON.parse(user) : null;
}

function logout() {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(USER_KEY);
    window.location.href = '/register.html';
}

function requireAuth() {
    if (!getToken()) {
        window.location.href = '/register.html';
    }
}

function authHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + getToken()
    };
}

async function handleResponse(response) {
    const data = await response.json();
    if (!response.ok) {
        throw new Error(data.message || 'Something went wrong');
    }
    return data;
}