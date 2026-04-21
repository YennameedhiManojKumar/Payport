function goTo(action) {
  const container = document.getElementById('form-container');
  
  if (action === 'register') {
    container.innerHTML = `
      <h2>Register User</h2>
      <form id="registerForm">
        <input type="text" placeholder="Name" id="name" required /><br/><br/>
        <input type="text" placeholder="Mobile Number" id="mobile" required /><br/><br/>
        <button type="submit">Register</button>
      </form>
      <p id="registerMsg"></p>
    `;

    document.getElementById('registerForm').addEventListener('submit', async (e) => {
      e.preventDefault();
      const name = document.getElementById('name').value;
      const mobileNumber = document.getElementById('mobile').value;

      const response = await fetch("http://localhost:8080/api/user/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, mobileNumber })
      });

      if (response.ok) {
        const result = await response.json();
        document.getElementById('registerMsg').innerText = `✅ Registered: ID ${result.id}, Name: ${result.name}`;
      } else {
        const error = await response.json();
        document.getElementById('registerMsg').innerText = `❌ Failed: ${error.message || 'Server error'}`;
      }
    });
  }

  else {
    container.innerHTML = `<h2>Coming soon: ${action}</h2>`;
  }
}
