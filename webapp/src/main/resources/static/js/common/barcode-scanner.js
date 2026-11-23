window.Is009Scanner = (function () {
    "use strict";

    const USB_MAX_AVG_INTERVAL_MS = 45;
    let html5QrScanner = null;
    let cameraActive = false;
    let lastCameraCode = "";
    let lastCameraTime = 0;

    function playBeep() {
        try {
            const AudioCtx = window.AudioContext || window.webkitAudioContext;
            if (!AudioCtx) return;
            const ctx = new AudioCtx();
            const osc = ctx.createOscillator();
            const gain = ctx.createGain();
            osc.type = "sine";
            osc.frequency.setValueAtTime(880, ctx.currentTime);
            gain.gain.setValueAtTime(0.12, ctx.currentTime);
            osc.connect(gain);
            gain.connect(ctx.destination);
            osc.start();
            osc.stop(ctx.currentTime + 0.08);
        } catch (e) {
            // ignore audio error
        }
    }

    function initScanInput(inputElement, options) {
        if (!inputElement || inputElement.dataset.scannerBound === "true") return;
        inputElement.dataset.scannerBound = "true";

        const opts = Object.assign({
            autoKeepFocus: false,
            clearOnScan: true
        }, options || {});

        let keyTimes = [];

        inputElement.addEventListener("keydown", function (e) {
            const now = performance.now();
            if (e.key === "Enter") {
                e.preventDefault();
                const rawVal = inputElement.value.trim();
                if (!rawVal) return;

                let isUsbScanner = false;
                if (keyTimes.length >= 5) {
                    const totalTime = keyTimes[keyTimes.length - 1] - keyTimes[0];
                    const avgDelta = totalTime / (keyTimes.length - 1);
                    isUsbScanner = avgDelta <= USB_MAX_AVG_INTERVAL_MS;
                }
                keyTimes = [];
                if (opts.clearOnScan) {
                    inputElement.value = "";
                }
                dispatchScanEvent(inputElement, rawVal, isUsbScanner ? "USB_SCANNER" : "MANUAL");
            } else if (e.key.length === 1) {
                keyTimes.push(now);
                if (keyTimes.length > 50) {
                    keyTimes.shift();
                }
            }
        });

        if (opts.autoKeepFocus) {
            inputElement.focus();
            document.addEventListener("click", function (ev) {
                const tag = ev.target && ev.target.tagName ? ev.target.tagName.toUpperCase() : "";
                const inModal = ev.target && ev.target.closest && ev.target.closest(".modal.show");
                if (inModal) return;
                if (["INPUT", "SELECT", "TEXTAREA", "BUTTON", "A", "LABEL"].includes(tag)) {
                    return;
                }
                inputElement.focus();
            });
        }
    }

    function dispatchScanEvent(targetEl, code, source) {
        const evt = new CustomEvent("barcode-scanned", {
            bubbles: true,
            detail: {
                code: code,
                source: source,
                timestamp: new Date().toISOString()
            }
        });
        (targetEl || document).dispatchEvent(evt);
    }

    function isCameraSupportedOrigin() {
        const host = window.location.hostname;
        return window.location.protocol === "https:"
            || host === "localhost"
            || host === "127.0.0.1";
    }

    async function toggleCameraScanner(containerId, toggleBtnEl, targetInputEl, customCallback) {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (!isCameraSupportedOrigin()) {
            Is009Common.showAlert(
                "warning",
                "Quét mã vạch qua Camera chỉ hoạt động trên HTTPS hoặc localhost.",
                "Vui lòng truy cập qua http://localhost:8080 hoặc sử dụng đầu đọc mã vạch USB."
            );
            return;
        }

        if (cameraActive && html5QrScanner) {
            try {
                await html5QrScanner.stop();
                html5QrScanner.clear();
            } catch (err) {
                // ignore stop error
            }
            html5QrScanner = null;
            cameraActive = false;
            container.classList.add("d-none");
            container.style.display = "none";
            if (toggleBtnEl) {
                toggleBtnEl.innerHTML = '<i class="bi bi-camera-video me-1"></i>Bật Camera quét mã';
                toggleBtnEl.classList.replace("btn-danger", "btn-outline-primary");
            }
            return;
        }

        if (typeof window.Html5Qrcode === "undefined") {
            Is009Common.showAlert(
                "warning",
                "Thư viện quét camera (html5-qrcode) chưa sẵn sàng.",
                "Kiểm tra kết nối mạng CDN hoặc nhập/quét bằng máy quét USB."
            );
            return;
        }

        container.classList.remove("d-none");
        container.style.display = "block";
        html5QrScanner = new window.Html5Qrcode(containerId);

        const scanConfig = {
            fps: 12,
            qrbox: { width: 280, height: 150 }
        };

        const onScanSuccess = function (decodedText) {
            if (!decodedText) return;
            const cleanText = decodedText.trim();
            if (!cleanText) return;

            const now = Date.now();
            if (cleanText === lastCameraCode && (now - lastCameraTime) < 1800) {
                return;
            }
            lastCameraCode = cleanText;
            lastCameraTime = now;

            playBeep();

            if (targetInputEl && typeof targetInputEl.value === "string") {
                targetInputEl.value = cleanText;
            }

            dispatchScanEvent(targetInputEl || document, cleanText, "CAMERA");
            if (typeof customCallback === "function") {
                customCallback(cleanText);
            }
        };

        const onScanFailure = function () {
            // ignore frame errors
        };

        try {
            try {
                await html5QrScanner.start({ facingMode: "environment" }, scanConfig, onScanSuccess, onScanFailure);
            } catch (envErr) {
                await html5QrScanner.start({ facingMode: "user" }, scanConfig, onScanSuccess, onScanFailure);
            }
            cameraActive = true;
            if (toggleBtnEl) {
                toggleBtnEl.innerHTML = '<i class="bi bi-camera-video-off me-1"></i>Tắt Camera';
                toggleBtnEl.classList.replace("btn-outline-primary", "btn-danger");
            }
        } catch (err) {
            container.classList.add("d-none");
            container.style.display = "none";
            Is009Common.showAlert(
                "danger",
                "Không thể mở Camera thiết bị: " + (err.message || err),
                "Cấp quyền truy cập Camera trên trình duyệt hoặc sử dụng máy quét USB."
            );
        }
    }

    function toggleCamera(containerId, callback) {
        return toggleCameraScanner(containerId, null, document, callback);
    }

    function renderBarcodeSvg(svgElement, codeValue, formatType) {
        if (!svgElement || typeof window.JsBarcode === "undefined") return;
        const cleanCode = String(codeValue || "").trim();
        if (!cleanCode) return;
        const useEan13 = (formatType === "EAN13") && window.Is009Ean13 && Is009Ean13.isValid(cleanCode);
        try {
            window.JsBarcode(svgElement, cleanCode, {
                format: useEan13 ? "EAN13" : "CODE128",
                width: 1.6,
                height: 44,
                displayValue: true,
                font: "Times New Roman",
                fontSize: 12,
                margin: 4
            });
        } catch (err) {
            window.JsBarcode(svgElement, cleanCode, {
                format: "CODE128",
                width: 1.5,
                height: 44,
                displayValue: true,
                font: "Times New Roman",
                fontSize: 12,
                margin: 4
            });
        }
    }

    return {
        initScanInput,
        initInput: function (el) { return initScanInput(el, { autoKeepFocus: false, clearOnScan: false }); },
        dispatchScanEvent,
        toggleCameraScanner,
        toggleCamera,
        renderBarcodeSvg
    };
})();

window.BarcodeScanner = window.Is009Scanner;
