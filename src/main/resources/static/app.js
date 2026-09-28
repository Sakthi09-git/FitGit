/**
 * FitLog — Personal Workout and Calorie Tracker
 * Frontend Client Logic (Vanilla JS + Fetch API)
 */

const API_BASE = '/api';

// Current State
let currentUser = null;
let allUsers = [];
let allWorkouts = [];
let allMeals = [];

// DOM Elements
const activeUserSelect = document.getElementById('activeUserSelect');
const dailyDateInput = document.getElementById('dailyDateInput');
const weeklyStartDateInput = document.getElementById('weeklyStartDateInput');
const goalWeekStartInput = document.getElementById('goalWeekStartInput');

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    setupTabNavigation();
    loadUsers();

    // Event listeners
    activeUserSelect.addEventListener('change', (e) => {
        const selectedId = Number(e.target.value);
        currentUser = allUsers.find(u => u.id === selectedId) || null;
        if (currentUser) {
            refreshAllData();
        }
    });

    document.getElementById('btnRefreshDaily').addEventListener('click', () => loadDailySummary());
    dailyDateInput.addEventListener('change', () => loadDailySummary());

    document.getElementById('btnFetchWeekly').addEventListener('click', () => loadWeeklySummary());
    document.getElementById('btnFetchGoalProgress').addEventListener('click', () => loadGoalProgress());
});

// ==============================================================================
// Tab Navigation
// ==============================================================================
function setupTabNavigation() {
    const tabBtns = document.querySelectorAll('.tab-btn');
    const tabPanes = document.querySelectorAll('.tab-pane');

    tabBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const targetTabId = btn.getAttribute('data-tab');

            tabBtns.forEach(b => b.classList.remove('active'));
            tabPanes.forEach(p => p.classList.remove('active'));

            btn.classList.add('active');
            const targetPane = document.getElementById(targetTabId);
            if (targetPane) {
                targetPane.classList.add('active');
            }
        });
    });
}

// ==============================================================================
// Users Management
// ==============================================================================
async function loadUsers() {
    try {
        const res = await fetch(`${API_BASE}/users`);
        if (!res.ok) throw new Error('Failed to load users');
        allUsers = await res.json();

        activeUserSelect.innerHTML = '';
        allUsers.forEach(user => {
            const opt = document.createElement('option');
            opt.value = user.id;
            opt.textContent = `${user.name} (${user.email})`;
            activeUserSelect.appendChild(opt);
        });

        if (allUsers.length > 0) {
            currentUser = allUsers[0];
            activeUserSelect.value = currentUser.id;
            document.getElementById('statTotalUsers').textContent = allUsers.length;
            refreshAllData();
        }
    } catch (err) {
        showToast('Error loading users: ' + err.message, 'error');
    }
}

// Refresh all sections when user or data changes
function refreshAllData() {
    if (!currentUser) return;
    loadDailySummary();
    loadWorkouts();
    loadMeals();
    loadWeeklySummary();
    loadGoalProgress();
}

// ==============================================================================
// Daily Summary
// ==============================================================================
async function loadDailySummary() {
    if (!currentUser) return;
    const date = dailyDateInput.value;
    if (!date) return;

    try {
        const res = await fetch(`${API_BASE}/users/${currentUser.id}/summary/daily?date=${date}`);
        if (!res.ok) throw new Error('Failed to fetch daily summary');
        const data = await res.json();

        document.getElementById('dailyCalConsumed').textContent = data.caloriesConsumed.toLocaleString();
        document.getElementById('dailyCalBurned').textContent = data.caloriesBurned.toLocaleString();
        document.getElementById('dailyNetCal').textContent = data.netCalories.toLocaleString();

        const netBadge = document.getElementById('netBadge');
        if (data.netCalories > 0) {
            netBadge.textContent = 'Calorie Surplus';
            netBadge.className = 'badge in-badge';
        } else if (data.netCalories < 0) {
            netBadge.textContent = 'Calorie Deficit';
            netBadge.className = 'badge out-badge';
        } else {
            netBadge.textContent = 'Balanced';
            netBadge.className = 'badge net-badge';
        }

        // Also update mini lists for today
        loadDailyMiniLists(date);
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function loadDailyMiniLists(date) {
    if (!currentUser) return;
    try {
        const [wRes, mRes] = await Promise.all([
            fetch(`${API_BASE}/users/${currentUser.id}/workouts?date=${date}`),
            fetch(`${API_BASE}/users/${currentUser.id}/meals?date=${date}`)
        ]);

        const workouts = wRes.ok ? await wRes.json() : [];
        const meals = mRes.ok ? await mRes.json() : [];

        // Render Workouts
        const wContainer = document.getElementById('dailyWorkoutsList');
        if (workouts.length === 0) {
            wContainer.innerHTML = '<p class="empty-state">No workouts logged on this date.</p>';
        } else {
            wContainer.innerHTML = workouts.map(w => `
                <div class="mini-item-card">
                    <div>
                        <div class="mini-item-title">${w.workoutType} (${w.durationMinutes}m)</div>
                        <div class="mini-item-sub">${w.notes || 'No notes'}</div>
                    </div>
                    <div class="mini-item-val text-amber">-${w.caloriesBurned} kcal</div>
                </div>
            `).join('');
        }

        // Render Meals
        const mContainer = document.getElementById('dailyMealsList');
        if (meals.length === 0) {
            mContainer.innerHTML = '<p class="empty-state">No meals logged on this date.</p>';
        } else {
            mContainer.innerHTML = meals.map(m => `
                <div class="mini-item-card">
                    <div>
                        <div class="mini-item-title">${m.foodItem} (${m.mealType})</div>
                        <div class="mini-item-sub">${m.quantity || ''}</div>
                    </div>
                    <div class="mini-item-val text-emerald">+${m.calories} kcal</div>
                </div>
            `).join('');
        }
    } catch (e) {
        console.error('Error loading mini lists', e);
    }
}

// ==============================================================================
// Workouts Section
// ==============================================================================
async function loadWorkouts() {
    if (!currentUser) return;
    const tbody = document.getElementById('workoutsTableBody');
    tbody.innerHTML = '<tr><td colspan="7" class="loading-state">Loading workouts...</td></tr>';

    try {
        const res = await fetch(`${API_BASE}/users/${currentUser.id}/workouts`);
        if (!res.ok) throw new Error('Failed to load workouts');
        allWorkouts = await res.json();

        document.getElementById('statTotalWorkouts').textContent = allWorkouts.length;

        if (allWorkouts.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="empty-state">No workouts recorded yet. Click "Log New Workout" to start!</td></tr>';
            return;
        }

        tbody.innerHTML = allWorkouts.map(w => `
            <tr>
                <td>#${w.id}</td>
                <td><span class="type-pill pill-${w.workoutType.toLowerCase()}">${w.workoutType}</span></td>
                <td>${w.durationMinutes} mins</td>
                <td><strong class="text-amber">${w.caloriesBurned} kcal</strong></td>
                <td>${w.workoutDate}</td>
                <td>${w.notes || '—'}</td>
                <td>
                    <div class="action-btns">
                        <button class="btn btn-secondary btn-xs" onclick="openEditWorkoutModal(${w.id})">Edit</button>
                        <button class="btn btn-danger btn-xs" onclick="deleteWorkout(${w.id})">Delete</button>
                    </div>
                </td>
            </tr>
        `).join('');
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="7" class="empty-state text-danger">${err.message}</td></tr>`;
    }
}

function openAddWorkoutModal() {
    document.getElementById('workoutModalTitle').textContent = 'Log New Workout';
    document.getElementById('formWorkoutId').value = '';
    document.getElementById('formWorkoutType').value = 'RUNNING';
    document.getElementById('formWorkoutDuration').value = 30;
    document.getElementById('formWorkoutCalories').value = 250;
    document.getElementById('formWorkoutDate').value = dailyDateInput.value || '2026-09-28';
    document.getElementById('formWorkoutNotes').value = '';
    document.getElementById('workoutModal').classList.add('active');
}

function openEditWorkoutModal(id) {
    const workout = allWorkouts.find(w => w.id === id);
    if (!workout) return;

    document.getElementById('workoutModalTitle').textContent = `Edit Workout #${id}`;
    document.getElementById('formWorkoutId').value = workout.id;
    document.getElementById('formWorkoutType').value = workout.workoutType;
    document.getElementById('formWorkoutDuration').value = workout.durationMinutes;
    document.getElementById('formWorkoutCalories').value = workout.caloriesBurned;
    document.getElementById('formWorkoutDate').value = workout.workoutDate;
    document.getElementById('formWorkoutNotes').value = workout.notes || '';
    document.getElementById('workoutModal').classList.add('active');
}

function closeWorkoutModal() {
    document.getElementById('workoutModal').classList.remove('active');
}

async function handleSaveWorkout(e) {
    e.preventDefault();
    if (!currentUser) return;

    const id = document.getElementById('formWorkoutId').value;
    const payload = {
        userId: currentUser.id,
        workoutType: document.getElementById('formWorkoutType').value,
        durationMinutes: parseInt(document.getElementById('formWorkoutDuration').value, 10),
        caloriesBurned: parseInt(document.getElementById('formWorkoutCalories').value, 10),
        workoutDate: document.getElementById('formWorkoutDate').value,
        notes: document.getElementById('formWorkoutNotes').value
    };

    try {
        let res;
        if (id) {
            res = await fetch(`${API_BASE}/workouts/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
        } else {
            res = await fetch(`${API_BASE}/workouts`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
        }

        const data = await res.json();
        if (!res.ok) {
            throw new Error(data.message || 'Failed to save workout');
        }

        showToast(id ? 'Workout updated successfully!' : 'Workout logged successfully!', 'success');
        closeWorkoutModal();
        refreshAllData();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function deleteWorkout(id) {
    if (!confirm(`Are you sure you want to delete Workout #${id}?`)) return;

    try {
        const res = await fetch(`${API_BASE}/workouts/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Failed to delete workout');
        showToast(`Workout #${id} deleted.`, 'success');
        refreshAllData();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// ==============================================================================
// Meals Section
// ==============================================================================
async function loadMeals() {
    if (!currentUser) return;
    const tbody = document.getElementById('mealsTableBody');
    tbody.innerHTML = '<tr><td colspan="8" class="loading-state">Loading meals...</td></tr>';

    try {
        const res = await fetch(`${API_BASE}/users/${currentUser.id}/meals`);
        if (!res.ok) throw new Error('Failed to load meals');
        allMeals = await res.json();

        document.getElementById('statTotalMeals').textContent = allMeals.length;

        if (allMeals.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="empty-state">No meals recorded yet. Click "Log New Meal" to start!</td></tr>';
            return;
        }

        tbody.innerHTML = allMeals.map(m => `
            <tr>
                <td>#${m.id}</td>
                <td><span class="type-pill pill-${m.mealType.toLowerCase()}">${m.mealType}</span></td>
                <td><strong>${m.foodItem}</strong></td>
                <td>${m.quantity || '—'}</td>
                <td><strong class="text-emerald">${m.calories} kcal</strong></td>
                <td>${m.mealDate}</td>
                <td>${m.notes || '—'}</td>
                <td>
                    <div class="action-btns">
                        <button class="btn btn-secondary btn-xs" onclick="openEditMealModal(${m.id})">Edit</button>
                        <button class="btn btn-danger btn-xs" onclick="deleteMeal(${m.id})">Delete</button>
                    </div>
                </td>
            </tr>
        `).join('');
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="8" class="empty-state text-danger">${err.message}</td></tr>`;
    }
}

function openAddMealModal() {
    document.getElementById('mealModalTitle').textContent = 'Log New Meal';
    document.getElementById('formMealId').value = '';
    document.getElementById('formMealType').value = 'BREAKFAST';
    document.getElementById('formMealFoodItem').value = '';
    document.getElementById('formMealQuantity').value = '';
    document.getElementById('formMealCalories').value = 300;
    document.getElementById('formMealDate').value = dailyDateInput.value || '2026-09-28';
    document.getElementById('formMealNotes').value = '';
    document.getElementById('mealModal').classList.add('active');
}

function openEditMealModal(id) {
    const meal = allMeals.find(m => m.id === id);
    if (!meal) return;

    document.getElementById('mealModalTitle').textContent = `Edit Meal #${id}`;
    document.getElementById('formMealId').value = meal.id;
    document.getElementById('formMealType').value = meal.mealType;
    document.getElementById('formMealFoodItem').value = meal.foodItem;
    document.getElementById('formMealQuantity').value = meal.quantity || '';
    document.getElementById('formMealCalories').value = meal.calories;
    document.getElementById('formMealDate').value = meal.mealDate;
    document.getElementById('formMealNotes').value = meal.notes || '';
    document.getElementById('mealModal').classList.add('active');
}

function closeMealModal() {
    document.getElementById('mealModal').classList.remove('active');
}

async function handleSaveMeal(e) {
    e.preventDefault();
    if (!currentUser) return;

    const id = document.getElementById('formMealId').value;
    const payload = {
        userId: currentUser.id,
        mealType: document.getElementById('formMealType').value,
        foodItem: document.getElementById('formMealFoodItem').value,
        quantity: document.getElementById('formMealQuantity').value,
        calories: parseInt(document.getElementById('formMealCalories').value, 10),
        mealDate: document.getElementById('formMealDate').value,
        notes: document.getElementById('formMealNotes').value
    };

    try {
        let res;
        if (id) {
            res = await fetch(`${API_BASE}/meals/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
        } else {
            res = await fetch(`${API_BASE}/meals`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
        }

        const data = await res.json();
        if (!res.ok) {
            throw new Error(data.message || 'Failed to save meal');
        }

        showToast(id ? 'Meal updated successfully!' : 'Meal logged successfully!', 'success');
        closeMealModal();
        refreshAllData();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function deleteMeal(id) {
    if (!confirm(`Are you sure you want to delete Meal #${id}?`)) return;

    try {
        const res = await fetch(`${API_BASE}/meals/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Failed to delete meal');
        showToast(`Meal #${id} deleted.`, 'success');
        refreshAllData();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// ==============================================================================
// Weekly Summary & Trend (Enforces Mandatory Rule 2)
// ==============================================================================
async function loadWeeklySummary() {
    if (!currentUser) return;
    const weekStart = weeklyStartDateInput.value;
    if (!weekStart) return;

    const tbody = document.getElementById('weeklyDailyBreakdownBody');

    try {
        const res = await fetch(`${API_BASE}/users/${currentUser.id}/summary/weekly?weekStart=${weekStart}`);
        const data = await res.json();

        if (!res.ok) {
            // Mandatory Rule 2 handled here
            document.getElementById('weeklyCalConsumed').textContent = '—';
            document.getElementById('weeklyCalBurned').textContent = '—';
            document.getElementById('weeklyNetCal').textContent = '—';
            document.getElementById('weeklyWorkoutCount').textContent = '—';

            tbody.innerHTML = `<tr><td colspan="6" class="empty-state text-danger">${data.message || 'No data found.'}</td></tr>`;
            showToast(`Rule 2 Enforced: ${data.message}`, 'error');
            return;
        }

        document.getElementById('weeklyCalConsumed').textContent = data.totalCaloriesConsumed.toLocaleString() + ' kcal';
        document.getElementById('weeklyCalBurned').textContent = data.totalCaloriesBurned.toLocaleString() + ' kcal';
        document.getElementById('weeklyNetCal').textContent = data.netCalories.toLocaleString() + ' kcal';
        document.getElementById('weeklyWorkoutCount').textContent = `${data.totalWorkouts} sessions`;

        // Render 7-day breakdown table
        if (data.dailyBreakdown && data.dailyBreakdown.length > 0) {
            tbody.innerHTML = data.dailyBreakdown.map(d => `
                <tr>
                    <td><strong>${d.date}</strong></td>
                    <td class="text-emerald">+${d.caloriesConsumed} kcal</td>
                    <td class="text-amber">-${d.caloriesBurned} kcal</td>
                    <td><strong>${d.netCalories} kcal</strong></td>
                    <td>${d.workoutCount}</td>
                    <td>${d.mealCount}</td>
                </tr>
            `).join('');
        }
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="6" class="empty-state text-danger">${err.message}</td></tr>`;
    }
}

// ==============================================================================
// Weekly Goal & Progress
// ==============================================================================
async function loadGoalProgress() {
    if (!currentUser) return;
    const weekStart = goalWeekStartInput.value;
    if (!weekStart) return;

    const badge = document.getElementById('goalBadge');
    const bar = document.getElementById('goalProgressBar');

    try {
        const res = await fetch(`${API_BASE}/users/${currentUser.id}/goals/progress?weekStart=${weekStart}`);
        const data = await res.json();

        if (!res.ok) {
            badge.textContent = 'No Goal Set';
            badge.className = 'goal-badge';
            bar.style.width = '0%';
            document.getElementById('goalCompletedCount').textContent = '0';
            document.getElementById('goalTargetCount').textContent = '0';
            document.getElementById('goalRemainingCount').textContent = '0';
            document.getElementById('goalPercentage').textContent = '0%';
            document.getElementById('statGoalTarget').textContent = '—';
            return;
        }

        document.getElementById('goalCompletedCount').textContent = data.completed;
        document.getElementById('goalTargetCount').textContent = data.target;
        document.getElementById('goalRemainingCount').textContent = data.remaining;
        document.getElementById('statGoalTarget').textContent = `${data.target} workouts`;

        const pct = Math.min(100, Math.round((data.completed / data.target) * 100));
        document.getElementById('goalPercentage').textContent = `${pct}%`;
        bar.style.width = `${pct}%`;

        if (data.goalAchieved) {
            badge.textContent = '🎉 Goal Achieved!';
            badge.className = 'goal-badge goal-achieved';
        } else {
            badge.textContent = `In Progress (${data.remaining} to go)`;
            badge.className = 'goal-badge goal-in-progress';
        }
    } catch (err) {
        console.error('Error loading goal progress', err);
    }
}

async function handleSaveGoal(e) {
    e.preventDefault();
    if (!currentUser) return;

    const payload = {
        userId: currentUser.id,
        weeklyWorkoutTarget: parseInt(document.getElementById('formGoalTarget').value, 10),
        weekStartDate: document.getElementById('formGoalWeekStart').value
    };

    try {
        const res = await fetch(`${API_BASE}/goals`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await res.json();
        if (!res.ok) {
            throw new Error(data.message || 'Failed to save goal');
        }

        showToast('Weekly workout goal saved successfully!', 'success');
        goalWeekStartInput.value = payload.weekStartDate;
        loadGoalProgress();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// ==============================================================================
// Viva Demonstration & Negative Testing Lab
// ==============================================================================
function logToTerminal(text) {
    const term = document.getElementById('terminalOutput');
    const timestamp = new Date().toLocaleTimeString();
    term.textContent = `[${timestamp}] ${text}\n\n` + term.textContent;
}

function clearTestTerminal() {
    document.getElementById('terminalOutput').textContent = '// Terminal cleared. Ready for next test.';
}

async function runNegativeWorkoutCalorieTest() {
    logToTerminal('Running Test: POST /api/workouts with negative caloriesBurned (-100)...');
    try {
        const res = await fetch(`${API_BASE}/workouts`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userId: currentUser ? currentUser.id : 1,
                workoutType: 'RUNNING',
                durationMinutes: 30,
                caloriesBurned: -100, // Invalid negative value
                workoutDate: '2026-09-28',
                notes: 'Viva Negative Test'
            })
        });
        const data = await res.json();
        logToTerminal(`Response (${res.status} ${res.statusText}):\n${JSON.stringify(data, null, 2)}`);
        if (res.status === 400) {
            showToast('Rule 1 Demonstrated: Negative workout calories rejected with 400!', 'success');
        }
    } catch (e) {
        logToTerminal(`Network Error: ${e.message}`);
    }
}

async function runNegativeMealCalorieTest() {
    logToTerminal('Running Test: POST /api/meals with negative calories (-500)...');
    try {
        const res = await fetch(`${API_BASE}/meals`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userId: currentUser ? currentUser.id : 1,
                mealType: 'BREAKFAST',
                foodItem: 'Negative Calorie Test',
                quantity: '1 serving',
                calories: -500, // Invalid negative value
                mealDate: '2026-09-28',
                notes: 'Viva Negative Test'
            })
        });
        const data = await res.json();
        logToTerminal(`Response (${res.status} ${res.statusText}):\n${JSON.stringify(data, null, 2)}`);
        if (res.status === 400) {
            showToast('Rule 1 Demonstrated: Negative meal calories rejected with 400!', 'success');
        }
    } catch (e) {
        logToTerminal(`Network Error: ${e.message}`);
    }
}

async function runEmptyWeekTest() {
    logToTerminal('Running Test: GET /api/users/1/summary/weekly?weekStart=2035-01-01 (Empty week)...');
    try {
        const res = await fetch(`${API_BASE}/users/1/summary/weekly?weekStart=2035-01-01`);
        const data = await res.json();
        logToTerminal(`Response (${res.status} ${res.statusText}):\n${JSON.stringify(data, null, 2)}`);
        if (res.status === 404) {
            showToast('Rule 2 Demonstrated: Empty week rejected with 404 Not Found!', 'success');
        }
    } catch (e) {
        logToTerminal(`Network Error: ${e.message}`);
    }
}

async function runZeroDurationTest() {
    logToTerminal('Running Test: POST /api/workouts with durationMinutes: 0...');
    try {
        const res = await fetch(`${API_BASE}/workouts`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userId: currentUser ? currentUser.id : 1,
                workoutType: 'WALKING',
                durationMinutes: 0, // Invalid duration
                caloriesBurned: 100,
                workoutDate: '2026-09-28',
                notes: 'Zero duration test'
            })
        });
        const data = await res.json();
        logToTerminal(`Response (${res.status} ${res.statusText}):\n${JSON.stringify(data, null, 2)}`);
        if (res.status === 400) {
            showToast('Rule 3 Demonstrated: Zero duration rejected with 400!', 'success');
        }
    } catch (e) {
        logToTerminal(`Network Error: ${e.message}`);
    }
}

async function runMissingUserTest() {
    logToTerminal('Running Test: POST /api/workouts for non-existent userId: 99999...');
    try {
        const res = await fetch(`${API_BASE}/workouts`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userId: 99999, // Non-existent user
                workoutType: 'RUNNING',
                durationMinutes: 30,
                caloriesBurned: 250,
                workoutDate: '2026-09-28'
            })
        });
        const data = await res.json();
        logToTerminal(`Response (${res.status} ${res.statusText}):\n${JSON.stringify(data, null, 2)}`);
        if (res.status === 404) {
            showToast('Rule 6 Demonstrated: Missing user rejected with 404 Not Found!', 'success');
        }
    } catch (e) {
        logToTerminal(`Network Error: ${e.message}`);
    }
}

async function runInvalidEmailTest() {
    logToTerminal('Running Test: POST /api/users with invalid email format ("not-an-email")...');
    try {
        const res = await fetch(`${API_BASE}/users`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                name: 'Bad Email User',
                email: 'not-an-email',
                phone: '1234567890'
            })
        });
        const data = await res.json();
        logToTerminal(`Response (${res.status} ${res.statusText}):\n${JSON.stringify(data, null, 2)}`);
        if (res.status === 400) {
            showToast('Bean Validation Demonstrated: Invalid email rejected with 400!', 'success');
        }
    } catch (e) {
        logToTerminal(`Network Error: ${e.message}`);
    }
}

// ==============================================================================
// Toast Notifications
// ==============================================================================
function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    const icon = type === 'success' ? '✅' : '⚠️';
    toast.innerHTML = `<span>${icon}</span><span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}
