async function run() {
  console.log("=== DIAGNOSTIC API TEST ===");
  
  // Test manager01 login
  console.log("\n1. Testing login with manager01 / 123...");
  try {
    const res = await fetch("http://localhost:5001/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: "manager01", password: "123" })
    });
    console.log("Status:", res.status);
    const data = await res.json();
    console.log("Response:", JSON.stringify(data, null, 2));

    if (res.ok && data.accessToken) {
      const token = data.accessToken;
      
      // Test fetching employees
      console.log("\n2. Fetching /api/employees...");
      const empRes = await fetch("http://localhost:5001/api/employees", {
        headers: { "Authorization": `Bearer ${token}` }
      });
      console.log("Status:", empRes.status);
      const empData = await empRes.json();
      console.log("Employees Count:", Array.isArray(empData) ? empData.length : "Not an array");
      if (!Array.isArray(empData)) {
        console.log("Response:", empData);
      }

      // Test fetching invoices
      console.log("\n3. Fetching /api/bookings/invoices...");
      const invRes = await fetch("http://localhost:5001/api/bookings/invoices", {
        headers: { "Authorization": `Bearer ${token}` }
      });
      console.log("Status:", invRes.status);
      const invData = await invRes.json();
      console.log("Invoices Count:", Array.isArray(invData) ? invData.length : "Not an array");
      if (!Array.isArray(invData)) {
        console.log("Response:", invData);
      }
    }
  } catch (err) {
    console.error("Error during manager01 test:", err.message);
  }

  // Test staff28 login
  console.log("\n4. Testing login with staff28 / 123...");
  try {
    const res = await fetch("http://localhost:5001/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: "staff28", password: "123" })
    });
    console.log("Status:", res.status);
    const data = await res.json();
    console.log("Response:", JSON.stringify(data, null, 2));
  } catch (err) {
    console.error("Error during staff28 test:", err.message);
  }
}

run();
