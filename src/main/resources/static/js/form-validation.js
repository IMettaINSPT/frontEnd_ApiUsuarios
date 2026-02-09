(() => {
  "use strict";

  // === Helpers ===
  const qsa = (root, sel) => Array.from(root.querySelectorAll(sel));

  const isEmpty = (v) => v == null || String(v).trim() === "";

  const toNumber = (v) => {
    const n = Number(v);
    return Number.isFinite(n) ? n : null;
  };

  function getFieldValue(el) {
    if (el.type === "checkbox") return el.checked ? "true" : "";
    if (el.type === "radio") {
      const checked = document.querySelector(`input[name="${CSS.escape(el.name)}"]:checked`);
      return checked ? checked.value : "";
    }
    return el.value ?? "";
  }

  function ensureErrorNode(el) {
    // Si ya hay un <div class="field-error"> al lado, lo reutilizamos
    let err = el.closest(".field")?.querySelector(".field-error")
      || el.parentElement?.querySelector(".field-error");

    if (!err) {
      err = document.createElement("div");
      err.className = "field-error";
      // lo insertamos debajo del campo
      (el.parentElement || el).appendChild(err);
    }
    return err;
  }

  function setFieldError(el, message) {
    const err = ensureErrorNode(el);
    err.textContent = message || "";
    err.style.display = message ? "block" : "none";
    el.classList.toggle("is-invalid", !!message);
  }

  function clearFieldError(el) {
    setFieldError(el, "");
  }

  // === Reglas ===
  function validateElement(el) {
    if (!el || el.disabled) return true;
    if (el.hasAttribute("data-skip-validation")) return true;

    const value = getFieldValue(el);
    const required = el.hasAttribute("data-required");

    // 1) required
    if (required && isEmpty(value)) {
      setFieldError(el, "Este campo es obligatorio.");
      return false;
    }

    // Si NO es required y está vacío, no validamos más reglas
    if (!required && isEmpty(value)) {
      clearFieldError(el);
      return true;
    }

    // 2) minlength / maxlength
    if (el.dataset.minlen) {
      const minlen = parseInt(el.dataset.minlen, 10);
      if (String(value).trim().length < minlen) {
        setFieldError(el, `Mínimo ${minlen} caracteres.`);
        return false;
      }
    }

    if (el.dataset.maxlen) {
      const maxlen = parseInt(el.dataset.maxlen, 10);
      if (String(value).trim().length > maxlen) {
        setFieldError(el, `Máximo ${maxlen} caracteres.`);
        return false;
      }
    }

    // 3) number min/max
    // Aplica a type number o si el campo tiene data-min/data-max
    if (el.dataset.min || el.dataset.max || el.type === "number") {
      const n = toNumber(value);
      if (n == null) {
        setFieldError(el, "Debe ser un número válido.");
        return false;
      }
      if (el.dataset.min) {
        const min = Number(el.dataset.min);
        if (n < min) {
          setFieldError(el, `El valor mínimo es ${min}.`);
          return false;
        }
      }
      if (el.dataset.max) {
        const max = Number(el.dataset.max);
        if (n > max) {
          setFieldError(el, `El valor máximo es ${max}.`);
          return false;
        }
      }
    }

    // 4) pattern (regex)
    if (el.dataset.pattern) {
      try {
        const re = new RegExp(el.dataset.pattern);
        if (!re.test(String(value).trim())) {
          setFieldError(el, el.dataset.patternMsg || "Formato inválido.");
          return false;
        }
      } catch (e) {
        // Si el regex está mal, no rompemos la app
        console.warn("Regex inválido en data-pattern:", el.dataset.pattern, e);
      }
    }

    // 5) email (si querés)
    if (el.dataset.type === "email" || el.type === "email") {
      const s = String(value).trim();
      const emailRe = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRe.test(s)) {
        setFieldError(el, "Email inválido.");
        return false;
      }
    }

    clearFieldError(el);
    return true;
  }

  function validateForm(form) {
    let ok = true;
    const fields = qsa(form, "input, select, textarea");

    fields.forEach((el) => {
      const fieldOk = validateElement(el);
      if (!fieldOk) ok = false;
    });

    return ok;
  }

  // === Auto-init ===
  function hookForm(form) {
    // Evitar doble hook
    if (form.dataset.validationHooked === "1") return;
    form.dataset.validationHooked = "1";

    // En submit
    form.addEventListener("submit", (e) => {
      const ok = validateForm(form);
      if (!ok) {
        e.preventDefault();
        e.stopPropagation();

        // Scroll al primer error
        const firstInvalid = form.querySelector(".is-invalid");
        if (firstInvalid) {
          firstInvalid.scrollIntoView({ behavior: "smooth", block: "center" });
          firstInvalid.focus?.();
        }
      }
    });

    // Validación "en vivo"
    const watch = (el) => {
      const evt = (el.tagName === "SELECT" || el.type === "checkbox") ? "change" : "input";
      el.addEventListener(evt, () => validateElement(el));
      el.addEventListener("blur", () => validateElement(el));
    };

    qsa(form, "input, select, textarea").forEach((el) => {
      if (el.hasAttribute("data-skip-validation")) return;
      watch(el);
    });
  }

  function init() {
    // Por defecto, validamos TODOS los forms que tengan:
    // - data-validate, o
    // - class="form" (tu convención)
    const forms = [
      ...qsa(document, "form[data-validate]"),
      ...qsa(document, "form.form"),
    ];

    // Deduplicar
    const unique = Array.from(new Set(forms));
    unique.forEach(hookForm);
  }

  document.addEventListener("DOMContentLoaded", init);
})();