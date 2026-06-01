import flatpickr from 'flatpickr';
import 'flatpickr/dist/flatpickr.min.css';

/**
 * Convert a Java DateTimeFormatter pattern to Flatpickr's dateFormat string and options.
 * Supports a subset of patterns sufficient for datetime fields.
 *
 * @see https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
 * @see https://flatpickr.js.org/formatting/
 *
 * @param {string} javaPattern
 * @returns {{ dateFormat: string, enableTime: boolean, time_24hr: boolean, enableSeconds: boolean }}
 */
function convertDateTimeFormat(javaPattern) {
  const usesAmPm = javaPattern.includes('a');
  const hasSeconds = javaPattern.includes('ss');

  // Use placeholder bytes to avoid conflicts between overlapping Java tokens
  // (MM=month vs mm=minute, HH=24-hour vs hh=12-hour).
  const dateFormat = javaPattern
    .replace(/yyyy/g, 'Y')
    .replace(/MM/g, '\x01')
    .replace(/dd/g, '\x02')
    .replace(/HH/g, usesAmPm ? '\x04' : '\x03') // 12h when AM/PM, else 24h
    .replace(/hh/g, '\x04')
    .replace(/mm/g, 'i')
    .replace(/ss/g, 'S')
    .replace(/a/g, 'K')
    .replace(/\x01/g, 'm')
    .replace(/\x02/g, 'd')
    .replace(/\x03/g, 'H')
    .replace(/\x04/g, 'h');

  return { dateFormat, enableTime: true, time_24hr: !usesAmPm, enableSeconds: hasSeconds };
}

document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('input[data-provide=datetimepicker]').forEach(function (input) {
    const javaFormat = input.dataset.dateTimeFormat || 'yyyy-MM-dd HH:mm:ss';
    const instance = flatpickr(input, {
      ...convertDateTimeFormat(javaFormat),
      allowInput: true
    });

    // Toggle calendar when the calendar icon is clicked
    const inputGroup = input.closest('.input-group.datetime-control');
    const icon = inputGroup && inputGroup.querySelector('.input-group-text');
    if (icon) {
      icon.addEventListener('click', function (evt) {
        evt.stopPropagation();
        instance.toggle();
      });
    }
  });
});
