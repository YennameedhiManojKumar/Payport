requireAuth();
const user = getUser();
if (user) document.getElementById('nav-user').textContent = user.name;

async function loadHistory() {
    const msg = document.getElementById('message');
    try {
        const data = await handleResponse(await fetch(
            `/api/transaction/history?upiId=${encodeURIComponent(user.upiId)}`,
            { headers: authHeaders() }
        ));

        const body = document.getElementById('historyBody');
        body.innerHTML = '';

        if (data.length === 0) {
            msg.textContent = 'No transactions yet.';
            msg.className = 'error';
            document.getElementById('historyTable').style.display = 'none';
            return;
        }

        data.forEach((tx, i) => {
            const date = new Date(tx.timestamp).toLocaleString('en-IN');
            const isSender = tx.senderUpi === user.upiId;
            const typeLabel = isSender
                ? `<span style="color:#ff6b6b">↑ Sent</span>`
                : `<span style="color:#00ffcc">↓ Received</span>`;
            body.innerHTML += `<tr>
                <td>${i + 1}</td>
                <td>${typeLabel}</td>
                <td>${tx.senderUpi}</td>
                <td>${tx.receiverUpi}</td>
                <td>₹${tx.amount.toFixed(2)}</td>
                <td>${date}</td>
            </tr>`;
        });

        document.getElementById('historyTable').style.display = 'block';
    } catch (err) {
        msg.textContent = err.message;
        msg.className = 'error';
    }
}

loadHistory();