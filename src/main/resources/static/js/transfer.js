requireAuth();
const user = getUser();
if (user) document.getElementById('nav-user').textContent = user.name;

document.getElementById('transferForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const msg = document.getElementById('message');
    msg.textContent = '';
    msg.className = '';

    try {
        await handleResponse(await fetch('/api/transaction/transfer', {
            method: 'POST',
            headers: authHeaders(),
            body: JSON.stringify({
                fromUpi: user.upiId,
                toUpi: document.getElementById('toUpi').value,
                amount: parseFloat(document.getElementById('amount').value),
                pin: document.getElementById('pin').value
            })
        }));
        msg.textContent = 'Transfer successful!';
        msg.className = 'success';
        document.getElementById('transferForm').reset();
    } catch (err) {
        msg.textContent = err.message;
        msg.className = 'error';
    }
});