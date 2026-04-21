document.getElementById("linkForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const userId = document.getElementById("userId").value;
    const upiId = document.getElementById("upiId").value;
    const balance = document.getElementById("balance").value;
    const status = document.getElementById("linkStatus");

    const params = new URLSearchParams({
        userId: userId,
        upiId: upiId,
        balance: balance
    });

    const response = await fetch(`/api/user/link-account?${params.toString()}`, {
        method: "POST"
    });

    status.className = ""; // clear previous style

    if (response.ok) {
        const data = await response.json();
        status.classList.add("alert", "success");
        status.innerText = `✅ Account linked with UPI: ${data.upiId}`;
    } else {
        status.classList.add("alert", "error");
        status.innerText = "❌ Failed to link account";
    }
});
