document.addEventListener("input", (event) => {
    if (!event.target.matches("[data-phone]")) return;

    const digits = event.target.value.replace(/\D/g, "").slice(0, 11);
    if (digits.length <= 2) {
        event.target.value = digits ? `(${digits}` : "";
        return;
    }

    const ddd = digits.slice(0, 2);
    const number = digits.slice(2);
    const splitAt = number.length > 8 ? 5 : 4;
    const first = number.slice(0, splitAt);
    const last = number.slice(splitAt);
    event.target.value = last ? `(${ddd}) ${first}-${last}` : `(${ddd}) ${first}`;
});
