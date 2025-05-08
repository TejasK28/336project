const contextPath = document.body.getAttribute('data-context-path');  // safer than inline JS
document.querySelectorAll('tbody tr').forEach(row => {
    const editBtn = row.querySelector('.edit-btn');
    const cancelBtn = row.querySelector('.cancel-btn');

    editBtn.addEventListener('click', event => {
        const editing = row.dataset.editing === 'true';
        if (!editing) {
            row.dataset.editing = 'true';
            editBtn.textContent = 'Save';
            cancelBtn.style.display = '';

            row.querySelectorAll('td.editable').forEach(td => {
                td.dataset.originalText = td.textContent.trim();
                const field = td.dataset.field;
                const value = td.dataset.originalText;
                td.innerHTML = `<input type="text" name="${field}" value="${value}" />`;
            });
            row.querySelectorAll('input[type="checkbox"]').forEach(cb => {
                cb.dataset.originalChecked = cb.checked;
                cb.disabled = false;
            });
        } else {
            const formData = new FormData();
            formData.append('username', row.dataset.username);
            row.querySelectorAll('td.editable input').forEach(input => {
                formData.append(input.name, input.value);
            });
            row.querySelectorAll('input[type="checkbox"]').forEach(cb => {
                formData.append(cb.name, cb.checked ? 'on' : 'off');
            });
			
			for (let pair of formData.entries()) {
			    console.log(pair[0]+ ': ' + pair[1]);
			}

			const contextPath = document.getElementById("contextPath").textContent;

            fetch(`${contextPath}/EditEmployee`, {
                method: 'POST',
                body: formData
            })
            .then(resp => {
                if (!resp.ok) throw new Error('Update failed');
                return resp.text();
            })
            .then(() => {
                row.dataset.editing = 'false';
                editBtn.textContent = 'Edit';
                cancelBtn.style.display = 'none';

                row.querySelectorAll('td.editable').forEach(td => {
                    const input = td.querySelector('input');
                    td.textContent = input.value;
                });
                row.querySelectorAll('input[type="checkbox"]').forEach(cb => {
                    cb.disabled = true;
                });
            })
            .catch(err => alert(err.message));
        }
    });

    cancelBtn.addEventListener('click', event => {
        row.dataset.editing = 'false';
        editBtn.textContent = 'Edit';
        cancelBtn.style.display = 'none';

        row.querySelectorAll('td.editable').forEach(td => {
            td.textContent = td.dataset.originalText;
        });
        row.querySelectorAll('input[type="checkbox"]').forEach(cb => {
            cb.checked = (cb.dataset.originalChecked === 'true');
            cb.disabled = true;
        });
    });
});
