document.getElementById("historyForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const upiId = document.getElementById("historyUpi").value;
    const result = document.getElementById("historyResult");

    const response = await fetch(`/api/transaction/history?upiId=${upiId}`);

    result.innerHTML = ""; // Clear previous results

    if (response.ok) {
        const transactions = await response.json();

        if (transactions.length === 0) {
            result.innerHTML = "<p>No transactions found.</p>";
            return;
        }

        const list = document.createElement("ul");

        transactions.forEach(tx => {
            const item = document.createElement("li");
            item.textContent = `${tx.timestamp} - ₹${tx.amount} from ${tx.senderUpi} to ${tx.receiverUpi}`;
            list.appendChild(item);
        });

        result.appendChild(list);
    } else {
        result.innerHTML = "<p class='alert error'>❌ Failed to fetch transaction history.</p>";
    }
});
