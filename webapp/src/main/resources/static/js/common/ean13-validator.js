window.Is009Ean13 = (function () {
    "use strict";

    function calcCheckDigit(ean12) {
        if (!ean12 || !/^\d{12}$/.test(ean12)) {
            return null;
        }
        let sum = 0;
        for (let i = 0; i < 12; i++) {
            const d = parseInt(ean12.charAt(i), 10);
            sum += ((i + 1) % 2 === 0) ? d * 3 : d;
        }
        return String((10 - (sum % 10)) % 10);
    }

    function isValid(ean13) {
        if (!ean13 || !/^\d{13}$/.test(ean13)) {
            return false;
        }
        const check = calcCheckDigit(ean13.substring(0, 12));
        return ean13.charAt(12) === check;
    }

    function suggest(ean12Or13) {
        if (!ean12Or13) return null;
        const clean = String(ean12Or13).trim();
        if (/^\d{12,13}$/.test(clean)) {
            const prefix = clean.substring(0, 12);
            return prefix + calcCheckDigit(prefix);
        }
        return null;
    }

    function bindEan13Validation(inputEl, selectLoaiEl, feedbackEl) {
        if (!inputEl || !feedbackEl) return;
        function validateNow() {
            const val = inputEl.value.trim();
            const loai = selectLoaiEl ? selectLoaiEl.value : "EAN13";
            if (!val) {
                inputEl.classList.remove("is-valid", "is-invalid");
                feedbackEl.textContent = "";
                return true;
            }
            if (loai !== "EAN13") {
                inputEl.classList.remove("is-invalid");
                inputEl.classList.add("is-valid");
                feedbackEl.className = "form-text text-success";
                feedbackEl.textContent = "Mã " + loai + " hợp lệ.";
                return true;
            }
            if (isValid(val)) {
                inputEl.classList.remove("is-invalid");
                inputEl.classList.add("is-valid");
                feedbackEl.className = "form-text text-success";
                feedbackEl.textContent = "Chuẩn EAN-13 hợp lệ (chữ số kiểm đúng).";
                return true;
            }
            inputEl.classList.remove("is-valid");
            inputEl.classList.add("is-invalid");
            feedbackEl.className = "form-text text-danger fw-semibold";
            const suggested = suggest(val);
            if (suggested) {
                feedbackEl.textContent = `Sai chữ số kiểm EAN-13! Gợi ý mã đúng: ${suggested}`;
            } else {
                feedbackEl.textContent = "Mã EAN-13 bắt buộc gồm đúng 13 chữ số (0-9).";
            }
            return false;
        }
        inputEl.addEventListener("input", validateNow);
        if (selectLoaiEl) {
            selectLoaiEl.addEventListener("change", validateNow);
        }
        return validateNow;
    }

    return {
        calcCheckDigit,
        isValid,
        suggest,
        bindEan13Validation
    };
})();
