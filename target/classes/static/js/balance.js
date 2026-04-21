document.getElementById("balanceForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const upiId = document.getElementById("balanceUpi").value;
    const result = document.getElementById("balanceResult");

    const response = await fetch(`/api/transaction/balance?upiId=${upiId}`);

    result.className = ""; // clear previous styling

    if (response.ok) {
        const balance = await response.text();
        result.classList.add("alert", "success");
        result.innerText = `💰 Balance for ${upiId}: ₹${balance}`;
    } else {
        result.classList.add("alert", "error");
        result.innerText = "❌ Failed to fetch balance.";
    }
});
