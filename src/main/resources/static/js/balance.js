requireAuth();
const user = getUser();
if (user) {
    document.getElementById('nav-user').textContent = user.name;
    document.getElementById('upiId').value = user.upiId;
}

document.getElementById('balanceForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const msg = document.getElementById('message');
    msg.textContent = '';
    msg.className = '';

    try {
        const data = await handleResponse(await fetch('/api/transaction/balance', {
            method: 'POST',
            headers: authHeaders(),
            body: JSON.stringify({
                upiId: user.upiId,
                pin: document.getElementById('pin').value
            })
        }));
        msg.textContent = `Balance: ₹${data.balance.toFixed(2)}`;
        msg.className = 'success';
    } catch (err) {
        msg.textContent = err.message;
        msg.className = 'error';
    }
});