document.getElementById("transferForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const fromUpi = document.getElementById("fromUpi").value;
    const toUpi = document.getElementById("toUpi").value;
    const amount = document.getElementById("amount").value;
    const status = document.getElementById("transferStatus");

    const params = new URLSearchParams({
        fromUpi: fromUpi,
        toUpi: toUpi,
        amount: amount
    });

    const response = await fetch(`/api/transaction/transfer?${params.toString()}`, {
        method: "POST"
    });

    status.className = ""; // clear old style

    if (response.ok) {
        const data = await response.text(); // because the API returns string, not JSON
        status.classList.add("alert", "success");
        status.innerText = `✅ ${data}`;
    } else {
        status.classList.add("alert", "error");
        status.innerText = "❌ Transfer failed.";
    }
});
