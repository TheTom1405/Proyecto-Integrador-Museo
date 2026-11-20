const resources = {
    museums: {
        title: "Museos",
        singular: "museo",
        description: "Administra la información y ubicación de los museos.",
        icon: "⌂",
        columns: [
            { key: "id", label: "ID" },
            { key: "name", label: "Nombre", primary: true },
            { key: "description", label: "Descripción" },
            { key: "latitude", label: "Latitud" },
            { key: "longitude", label: "Longitud" }
        ],
        fields: [
            { key: "name", label: "Nombre", required: true },
            { key: "description", label: "Descripción", type: "textarea" },
            { key: "latitude", label: "Latitud", type: "number", step: "any", min: -90, max: 90, required: true },
            { key: "longitude", label: "Longitud", type: "number", step: "any", min: -180, max: 180, required: true },
            { key: "landingPageUrl", label: "Sitio web", type: "url" }
        ]
    },
    categories: {
        title: "Categorías",
        singular: "categoría",
        description: "Organiza las piezas de la colección por tipo.",
        icon: "▤",
        columns: [
            { key: "id", label: "ID" },
            { key: "name", label: "Nombre", primary: true },
            { key: "description", label: "Descripción" }
        ],
        fields: [
            { key: "name", label: "Nombre", required: true },
            { key: "description", label: "Descripción", type: "textarea" }
        ]
    },
    artists: {
        title: "Artistas",
        singular: "artista",
        description: "Registra artistas y consulta su información biográfica.",
        icon: "✳",
        columns: [
            { key: "id", label: "ID" },
            { key: "name", label: "Nombre", primary: true },
            { key: "nationality", label: "Nacionalidad" },
            { key: "biography", label: "Biografía" }
        ],
        fields: [
            { key: "name", label: "Nombre", required: true },
            { key: "nationality", label: "Nacionalidad" },
            { key: "biography", label: "Biografía", type: "textarea" }
        ]
    },
    rooms: {
        title: "Salas",
        singular: "sala",
        description: "Administra las salas, su capacidad y el museo al que pertenecen.",
        icon: "▥",
        columns: [
            { key: "id", label: "ID" },
            { key: "name", label: "Nombre", primary: true },
            { key: "museum.name", label: "Museo" },
            { key: "capacity", label: "Capacidad" },
            { key: "description", label: "Descripción" }
        ],
        fields: [
            { key: "name", label: "Nombre", required: true },
            { key: "capacity", label: "Capacidad", type: "number", min: 1, step: 1, required: true },
            { key: "museum", label: "Museo", type: "relation", resource: "museums", required: true },
            { key: "description", label: "Descripción", type: "textarea" }
        ]
    },
    artifacts: {
        title: "Piezas",
        singular: "pieza",
        description: "Mantén el catálogo de piezas y su estado de conservación.",
        icon: "◇",
        columns: [
            { key: "id", label: "ID" },
            { key: "title", label: "Título", primary: true },
            { key: "category.name", label: "Categoría" },
            { key: "artist.name", label: "Artista" },
            { key: "currentRoom.name", label: "Sala" },
            { key: "conservationPercentage", label: "Conservación", suffix: "%" }
        ],
        fields: [
            { key: "title", label: "Título", required: true },
            { key: "conservationPercentage", label: "Conservación (%)", type: "number", min: 0, max: 100, step: 1, required: true },
            { key: "category", label: "Categoría", type: "relation", resource: "categories" },
            { key: "artist", label: "Artista", type: "relation", resource: "artists" },
            { key: "currentRoom", label: "Sala actual", type: "relation", resource: "rooms" },
            { key: "creationDate", label: "Fecha de creación", type: "datetime-local" },
            { key: "acquisitionDate", label: "Fecha de adquisición", type: "datetime-local" },
            { key: "imageUrl", label: "URL de imagen", type: "url" },
            { key: "description", label: "Descripción", type: "textarea" }
        ]
    },
    tours: {
        title: "Recorridos",
        singular: "recorrido",
        description: "Diseña recorridos y ordena las salas que se visitan.",
        icon: "↗",
        columns: [
            { key: "id", label: "ID" },
            { key: "name", label: "Nombre", primary: true },
            { key: "startDate", label: "Inicio" },
            { key: "endDate", label: "Fin" },
            { key: "rooms", label: "Salas", format: "room-names" }
        ],
        fields: [
            { key: "name", label: "Nombre", required: true },
            { key: "startDate", label: "Fecha de inicio", type: "datetime-local", required: true },
            { key: "endDate", label: "Fecha de fin", type: "datetime-local", required: true },
            { key: "rooms", label: "Salas del recorrido", type: "multi-relation", resource: "rooms", required: true },
            { key: "landingPageUrl", label: "Sitio web", type: "url" },
            { key: "description", label: "Descripción", type: "textarea" }
        ]
    }
};

const state = {
    current: "artifacts",
    records: {},
    editingId: null
};

const nav = document.querySelector("#resource-nav");
const title = document.querySelector("#page-title");
const description = document.querySelector("#page-description");
const notice = document.querySelector("#notice");
const editor = document.querySelector("#editor");
const tableContainer = document.querySelector("#table-container");
const recordCount = document.querySelector("#record-count");
const search = document.querySelector("#search");

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[character]);
}

function getPath(object, path) {
    return path.split(".").reduce((value, key) => value?.[key], object);
}

function showNotice(message, isError = false) {
    notice.textContent = message;
    notice.classList.toggle("error", isError);
    notice.hidden = !message;
}

async function request(path, options = {}) {
    const response = await fetch(path, {
        ...options,
        headers: {
            ...(options.body ? { "Content-Type": "application/json" } : {}),
            ...options.headers
        }
    });
    const text = await response.text();
    let result = text;
    if (text) {
        try {
            result = JSON.parse(text);
        } catch {
            result = text;
        }
    }
    if (!response.ok) {
        const message = typeof result === "string" ? result : JSON.stringify(result);
        throw new Error(message || `Error HTTP ${response.status}`);
    }
    return result;
}

async function loadRecords() {
    const entries = Object.keys(resources);
    const records = await Promise.all(entries.map(async key => [
        key,
        await request(`/${key}`)
    ]));
    state.records = Object.fromEntries(records);
}

function renderNavigation() {
    nav.innerHTML = Object.entries(resources).map(([key, config]) => `
        <button class="nav-item${key === state.current ? " active" : ""}" type="button" data-resource="${key}">
            <span class="nav-icon" aria-hidden="true">${escapeHtml(config.icon)}</span>
            <span>${escapeHtml(config.title)}</span>
        </button>
    `).join("");
}

function renderPage() {
    const config = resources[state.current];
    title.textContent = config.title;
    description.textContent = config.description;
    document.querySelector("#page-eyebrow").textContent = "GESTIÓN DEL MUSEO";
    document.querySelector("#list-title").textContent = config.title;
    document.title = `${config.title} | Museo`;
    search.value = "";
    renderNavigation();
    renderTable();
}

function renderTable() {
    const config = resources[state.current];
    const records = state.records[state.current] || [];
    const query = search.value.trim().toLocaleLowerCase("es");
    const filtered = records.filter(record =>
        !query || JSON.stringify(record).toLocaleLowerCase("es").includes(query)
    );

    recordCount.textContent = `${filtered.length} ${filtered.length === 1 ? "registro" : "registros"}`;
    if (filtered.length === 0) {
        tableContainer.innerHTML = `
            <div class="empty-state">
                <div>
                    <strong>${records.length ? "No hay coincidencias" : "Todavía no hay registros"}</strong>
                    <span>${records.length ? "Prueba con otro término de búsqueda." : `Crea el primer ${escapeHtml(config.singular)} para empezar.`}</span>
                </div>
            </div>`;
        return;
    }

    const columns = config.columns.map(column =>
        `<th scope="col">${escapeHtml(column.label)}</th>`
    ).join("");
    const rows = filtered.map(record => {
        const cells = config.columns.map(column => {
            let value;
            if (column.format === "room-names") {
                value = (record.rooms || []).map(room => room.name).filter(Boolean).join(", ");
            } else {
                value = getPath(record, column.key);
            }
            if (value === null || value === undefined || value === "") {
                value = "—";
            } else if (column.suffix) {
                value = `${value}${column.suffix}`;
            }
            const className = value === "—" ? "cell-muted" : (column.primary ? "cell-primary" : "");
            return `<td class="${className}">${escapeHtml(value)}</td>`;
        }).join("");
        return `
            <tr>
                ${cells}
                <td>
                    <div class="actions">
                        <button class="button button-secondary button-small" type="button" data-action="edit" data-id="${escapeHtml(record.id)}">Editar</button>
                        <button class="button button-danger button-small" type="button" data-action="delete" data-id="${escapeHtml(record.id)}">Eliminar</button>
                    </div>
                </td>
            </tr>`;
    }).join("");

    tableContainer.innerHTML = `
        <table>
            <thead><tr>${columns}<th scope="col">Acciones</th></tr></thead>
            <tbody>${rows}</tbody>
        </table>`;
}

function getFieldValue(field, record) {
    const value = record?.[field.key];
    if (field.type === "relation") {
        return value?.id ?? "";
    }
    if (field.type === "multi-relation") {
        return (value || []).map(item => String(item.id));
    }
    if (field.type === "datetime-local") {
        return value ? String(value).slice(0, 16) : "";
    }
    return value ?? "";
}

function renderField(field, record) {
    const value = getFieldValue(field, record);
    const required = field.required ? " required" : "";
    const fullWidth = field.type === "textarea" || field.type === "multi-relation" ? " full-width" : "";
    let control;

    if (field.type === "textarea") {
        control = `<textarea id="field-${field.key}" name="${field.key}"${required}>${escapeHtml(value)}</textarea>`;
    } else if (field.type === "relation" || field.type === "multi-relation") {
        const options = state.records[field.resource] || [];
        const selected = field.type === "multi-relation" ? value : [String(value)];
        control = `
            <select id="field-${field.key}" name="${field.key}"${field.type === "multi-relation" ? " multiple" : ""}${required}>
                ${field.type === "relation" ? `<option value="">Selecciona ${escapeHtml(resources[field.resource].singular)}</option>` : ""}
                ${options.map(option => `
                    <option value="${escapeHtml(option.id)}"${selected.includes(String(option.id)) ? " selected" : ""}>
                        ${escapeHtml(option.name)}
                    </option>`).join("")}
            </select>
            ${field.type === "multi-relation" ? '<span class="form-help">Mantén Ctrl (Windows) o Cmd (Mac) para elegir varias salas.</span>' : ""}`;
    } else {
        const attributes = [
            `type="${escapeHtml(field.type || "text")}"`,
            `id="field-${field.key}"`,
            `name="${field.key}"`,
            `value="${escapeHtml(value)}"`
        ];
        if (field.required) attributes.push("required");
        if (field.min !== undefined) attributes.push(`min="${field.min}"`);
        if (field.max !== undefined) attributes.push(`max="${field.max}"`);
        if (field.step !== undefined) attributes.push(`step="${field.step}"`);
        control = `<input ${attributes.join(" ")}>`;
    }

    return `
        <label class="form-field${fullWidth}" for="field-${field.key}">
            <span>${escapeHtml(field.label)}${field.required ? " *" : ""}</span>
            ${control}
        </label>`;
}

function openEditor(record = null) {
    const config = resources[state.current];
    state.editingId = record?.id ?? null;
    editor.innerHTML = `
        <div class="editor-heading">
            <div>
                <p class="eyebrow">${record ? "ACTUALIZAR INFORMACIÓN" : "NUEVO REGISTRO"}</p>
                <h2>${record ? "Editar" : "Agregar"} ${escapeHtml(config.singular)}</h2>
            </div>
            <button class="icon-button" type="button" data-action="close-editor" aria-label="Cerrar formulario">×</button>
        </div>
        <form id="record-form">
            <div class="form-grid">${config.fields.map(field => renderField(field, record)).join("")}</div>
            <div class="form-actions">
                <button class="button button-secondary" type="button" data-action="close-editor">Cancelar</button>
                <button class="button button-primary" type="submit">${record ? "Guardar cambios" : "Crear registro"}</button>
            </div>
        </form>`;
    editor.hidden = false;
    editor.scrollIntoView({ behavior: "smooth", block: "start" });
    editor.querySelector("input, select, textarea")?.focus();
}

function createPayload(form) {
    const formData = new FormData(form);
    const config = resources[state.current];
    const payload = {};
    for (const field of config.fields) {
        const control = form.elements.namedItem(field.key);
        if (field.type === "multi-relation") {
            payload[field.key] = [...control.selectedOptions].map(option => ({ id: Number(option.value) }));
        } else if (field.type === "relation") {
            payload[field.key] = formData.get(field.key) ? { id: Number(formData.get(field.key)) } : null;
        } else if (field.type === "number") {
            const value = formData.get(field.key);
            payload[field.key] = value === "" ? null : Number(value);
        } else {
            const value = formData.get(field.key);
            payload[field.key] = value === "" ? null : value;
        }
    }
    return payload;
}

async function saveRecord(form) {
    const key = state.current;
    const id = state.editingId;
    const method = id === null ? "POST" : "PUT";
    const path = id === null ? `/${key}` : `/${key}/${id}`;
    try {
        await request(path, {
            method,
            body: JSON.stringify(createPayload(form))
        });
    } catch (error) {
        showNotice(`No se pudo guardar: ${error.message}`, true);
        return;
    }

    editor.hidden = true;
    try {
        await loadRecords();
        renderPage();
        showNotice(id === null ? "Registro creado correctamente." : "Cambios guardados correctamente.");
    } catch (error) {
        showNotice(`El registro se guardó, pero no se pudo actualizar el listado: ${error.message}`, true);
    }
}

async function deleteRecord(id) {
    const config = resources[state.current];
    if (!window.confirm(`¿Eliminar este registro de ${config.title.toLocaleLowerCase("es")}? Esta acción no se puede deshacer.`)) {
        return;
    }
    try {
        await request(`/${state.current}/${id}`, { method: "DELETE" });
    } catch (error) {
        showNotice(`No se pudo eliminar: ${error.message}`, true);
        return;
    }

    editor.hidden = true;
    try {
        await loadRecords();
        renderPage();
        showNotice("Registro eliminado correctamente.");
    } catch (error) {
        showNotice(`El registro se eliminó, pero no se pudo actualizar el listado: ${error.message}`, true);
    }
}

nav.addEventListener("click", event => {
    const button = event.target.closest("[data-resource]");
    if (!button) return;
    state.current = button.dataset.resource;
    editor.hidden = true;
    showNotice("");
    renderPage();
});

document.querySelector("#new-record").addEventListener("click", () => {
    showNotice("");
    openEditor();
});

search.addEventListener("input", renderTable);

editor.addEventListener("click", event => {
    if (event.target.closest('[data-action="close-editor"]')) {
        editor.hidden = true;
    }
});

editor.addEventListener("submit", event => {
    if (event.target.id !== "record-form") return;
    event.preventDefault();
    saveRecord(event.target);
});

tableContainer.addEventListener("click", event => {
    const button = event.target.closest("[data-action]");
    if (!button) return;
    const record = (state.records[state.current] || []).find(item => String(item.id) === button.dataset.id);
    if (!record) return;
    if (button.dataset.action === "edit") openEditor(record);
    if (button.dataset.action === "delete") deleteRecord(record.id);
});

async function start() {
    renderNavigation();
    try {
        await loadRecords();
        renderPage();
    } catch (error) {
        recordCount.textContent = "Sin conexión";
        tableContainer.innerHTML = `
            <div class="empty-state">
                <div>
                    <strong>No se pudieron cargar los datos</strong>
                    <span>${escapeHtml(error.message)}<br>Confirma que Quarkus y MySQL estén ejecutándose e inténtalo de nuevo.</span>
                </div>
            </div>`;
        showNotice("No fue posible conectarse con la API del museo.", true);
    }
}

start();
