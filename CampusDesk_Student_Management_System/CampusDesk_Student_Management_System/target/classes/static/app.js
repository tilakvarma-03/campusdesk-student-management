const API = "/api/students";
const $ = (id) => document.getElementById(id);
let editingId = null;
let toastTimer;

function showToast(message, isError = false) {
  const toast = $("toast");
  toast.textContent = message;
  toast.className = "toast show" + (isError ? " error" : "");
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.className = "toast", 3200);
}

async function api(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: { "Content-Type": "application/json", ...(options.headers || {}) }
  });
  if (!response.ok) {
    let message = `Request failed (${response.status})`;
    try {
      const body = await response.json();
      if (body.message) message = body.message;
    } catch (_) {}
    throw new Error(message);
  }
  if (response.status === 204) return null;
  return response.json();
}

async function checkHealth() {
  const pill = $("connection");
  try {
    const health = await api("/api/health");
    pill.className = "connection-pill connected";
    pill.innerHTML = "<i></i><span>MySQL connected</span>";
    $("databaseStatus").textContent = "Connected";
    $("statusDetail").textContent = "Database is responding";
    $("statusPulse").className = "pulse connected";
    $("totalStudents").textContent = health.totalStudents;
  } catch (_) {
    pill.className = "connection-pill disconnected";
    pill.innerHTML = "<i></i><span>Database disconnected</span>";
    $("databaseStatus").textContent = "Disconnected";
    $("statusDetail").textContent = "Check MySQL and application settings";
    $("statusPulse").className = "pulse disconnected";
  }
}

async function loadStats() {
  try {
    const stats = await api(`${API}/stats`);
    $("totalStudents").textContent = stats.totalStudents;
    $("departmentCount").textContent = stats.departmentCount;
  } catch (_) {
    $("totalStudents").textContent = "—";
    $("departmentCount").textContent = "—";
  }
}

function initials(name) {
  return name.trim().split(/\s+/).slice(0, 2).map(part => part[0]).join("").toUpperCase();
}

async function loadStudents() {
  const search = $("searchInput").value.trim();
  const department = $("departmentFilter").value;
  const params = new URLSearchParams();
  if (search) params.set("search", search);
  if (department) params.set("department", department);

  try {
    const students = await api(`${API}?${params.toString()}`);
    const body = $("studentRows");
    body.innerHTML = "";
    if (!students.length) {
      body.innerHTML = '<tr><td colspan="4" class="empty-cell">No students found. Add a student to get started.</td></tr>';
    } else {
      students.forEach(student => {
        const row = document.createElement("tr");
        row.innerHTML = `
          <td><div class="student-name"><span class="student-avatar">${escapeHtml(initials(student.name))}</span><span>${escapeHtml(student.name)}<small>ID: ${student.id}</small></span></div></td>
          <td><span class="dept-badge">${escapeHtml(student.department)}</span></td>
          <td>${escapeHtml(student.email)}</td>
          <td><div class="row-actions"><button class="icon-btn" data-action="edit" data-id="${student.id}" title="Edit">Edit</button><button class="icon-btn delete" data-action="delete" data-id="${student.id}" title="Delete">Delete</button></div></td>`;
        body.appendChild(row);
      });
    }
    $("recordCount").textContent = `${students.length} record${students.length === 1 ? "" : "s"} shown`;
  } catch (error) {
    $("studentRows").innerHTML = `<tr><td colspan="4" class="empty-cell">${escapeHtml(error.message)}. Check database connection.</td></tr>`;
    $("recordCount").textContent = "Unable to load records";
  }
}

function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, char => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
  })[char]);
}

function resetForm() {
  $("studentForm").reset();
  $("studentId").disabled = false;
  editingId = null;
  $("formTitle").textContent = "Add a student";
  $("saveButton").innerHTML = "<span>＋</span> Add student";
  $("cancelEdit").hidden = true;
}

$("studentForm").addEventListener("submit", async event => {
  event.preventDefault();
  const student = {
    id: Number($("studentId").value),
    name: $("studentName").value.trim(),
    department: $("department").value,
    email: $("email").value.trim()
  };
  if (!Number.isInteger(student.id) || student.id < 1) {
    showToast("Enter a valid positive student ID.", true);
    return;
  }

  const button = $("saveButton");
  button.disabled = true;
  try {
    if (editingId === null) {
      await api(API, { method: "POST", body: JSON.stringify(student) });
      showToast("Student added successfully.");
    } else {
      await api(`${API}/${editingId}`, { method: "PUT", body: JSON.stringify(student) });
      showToast("Student details updated.");
    }
    resetForm();
    await refreshAll();
  } catch (error) {
    showToast(error.message, true);
  } finally {
    button.disabled = false;
  }
});

$("studentRows").addEventListener("click", async event => {
  const button = event.target.closest("button[data-action]");
  if (!button) return;
  const id = Number(button.dataset.id);
  if (button.dataset.action === "edit") {
    try {
      const student = await api(`${API}/${id}`);
      editingId = id;
      $("studentId").value = student.id;
      $("studentId").disabled = true;
      $("studentName").value = student.name;
      $("department").value = student.department;
      $("email").value = student.email;
      $("formTitle").textContent = "Edit student";
      $("saveButton").innerHTML = "Save changes";
      $("cancelEdit").hidden = false;
      $("studentName").focus();
      window.scrollTo({ top: 0, behavior: "smooth" });
    } catch (error) {
      showToast(error.message, true);
    }
  } else if (button.dataset.action === "delete") {
    if (!confirm(`Delete student ID ${id}? This cannot be undone.`)) return;
    try {
      await api(`${API}/${id}`, { method: "DELETE" });
      if (editingId === id) resetForm();
      showToast("Student deleted.");
      await refreshAll();
    } catch (error) {
      showToast(error.message, true);
    }
  }
});

$("cancelEdit").addEventListener("click", resetForm);
$("refreshButton").addEventListener("click", refreshAll);
$("departmentFilter").addEventListener("change", loadStudents);
let searchTimer;
$("searchInput").addEventListener("input", () => {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(loadStudents, 220);
});

async function refreshAll() {
  await Promise.all([checkHealth(), loadStats(), loadStudents()]);
}

refreshAll();
