document.addEventListener("DOMContentLoaded", function () {
    const transferForm = document.getElementById("transferForm");

    if (transferForm) {
        transferForm.addEventListener("submit", async function (e) {
            e.preventDefault();

            const fromUpi = document.getElementById("fromUpi").value;
            const toUpi = document.getElementById("toUpi").value;
            const amount = document.getElementById("amount").value;

            try {
                const response = await fetch(`http://localhost:8080/api/transaction/transfer?fromUpi=${fromUpi}&toUpi=${toUpi}&amount=${amount}`, {
                    method: "POST"
                });

                const message = await response.text();
                document.getElementById("transferMessage").textContent = message;
            } catch (error) {
                document.getElementById("transferMessage").textContent = "❌ Transfer failed.";
                console.error("Transfer Error:", error);
            }
        });
    }
});
