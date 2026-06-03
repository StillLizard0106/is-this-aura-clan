import React, { useState } from 'react';
import { ChevronLeft, ChevronRight, Clock } from 'lucide-react';

const DateTimePickerField = ({ value, onChange, min, max, label, hint }) => {
  const [showCalendar, setShowCalendar] = useState(false);
  const [displayMonth, setDisplayMonth] = useState(() => {
    if (value) {
      const [datePart] = value.split('T');
      const [year, month, day] = datePart.split('-').map(Number);
      return new Date(year, month - 1, 1);
    }
    return new Date();
  });

  const parseLocalDatetimeValue = (dateStr) => {
    if (!dateStr) return null;
    const [datePart, timePart] = dateStr.split('T');
    const [year, month, day] = datePart.split('-').map(Number);
    const [hours, minutes] = timePart ? timePart.split(':').map(Number) : [0, 0];
    return { year, month, day, hours, minutes };
  };

  const formatLocalDatetimeValue = (year, month, day, hours, minutes) => {
    const pad = (value) => String(value).padStart(2, '0');
    return `${year}-${pad(month)}-${pad(day)}T${pad(hours)}:${pad(minutes)}`;
  };

  const getMinDate = () => {
    if (!min) return null;
    const [datePart] = min.split('T');
    const [year, month, day] = datePart.split('-').map(Number);
    return new Date(year, month - 1, day);
  };

  const getMaxDate = () => {
    if (!max) return null;
    const [datePart] = max.split('T');
    const [year, month, day] = datePart.split('-').map(Number);
    return new Date(year, month - 1, day);
  };

  const getDaysInMonth = (date) => {
    return new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
  };

  const getFirstDayOfMonth = (date) => {
    return new Date(date.getFullYear(), date.getMonth(), 1).getDay();
  };

  const isDateDisabled = (year, month, day) => {
    const checkDate = new Date(year, month - 1, day);
    const minDate = getMinDate();
    const maxDate = getMaxDate();

    if (minDate && checkDate < minDate) return true;
    if (maxDate && checkDate > maxDate) return true;
    return false;
  };

  const isDateSelected = (year, month, day) => {
    if (!value) return false;
    const parsed = parseLocalDatetimeValue(value);
    return parsed && parsed.year === year && parsed.month === month && parsed.day === day;
  };

  const handleDateClick = (day) => {
    const parsed = parseLocalDatetimeValue(value);
    const hours = parsed?.hours ?? 12;
    const minutes = parsed?.minutes ?? 0;
    const newValue = formatLocalDatetimeValue(
      displayMonth.getFullYear(),
      displayMonth.getMonth() + 1,
      day,
      hours,
      minutes
    );
    onChange({ target: { value: newValue } });
  };

  const handleTimeChange = (field, val) => {
    const parsed = parseLocalDatetimeValue(value);
    if (!parsed) {
      const today = new Date();
      const newValue = formatLocalDatetimeValue(
        today.getFullYear(),
        today.getMonth() + 1,
        today.getDate(),
        field === 'hours' ? parseInt(val) : 0,
        field === 'minutes' ? parseInt(val) : 0
      );
      onChange({ target: { value: newValue } });
      return;
    }

    const newHours = field === 'hours' ? parseInt(val) : parsed.hours;
    const newMinutes = field === 'minutes' ? parseInt(val) : parsed.minutes;
    const newValue = formatLocalDatetimeValue(
      parsed.year,
      parsed.month,
      parsed.day,
      newHours,
      newMinutes
    );
    onChange({ target: { value: newValue } });
  };

  const handlePrevMonth = () => {
    setDisplayMonth(new Date(displayMonth.getFullYear(), displayMonth.getMonth() - 1, 1));
  };

  const handleNextMonth = () => {
    setDisplayMonth(new Date(displayMonth.getFullYear(), displayMonth.getMonth() + 1, 1));
  };

  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  const weekDays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  const daysInMonth = getDaysInMonth(displayMonth);
  const firstDay = getFirstDayOfMonth(displayMonth);
  const days = Array.from({ length: daysInMonth }, (_, i) => i + 1);
  const emptyDays = Array.from({ length: firstDay }, (_, i) => null);

  const parsed = parseLocalDatetimeValue(value);
  const hours12 = parsed ? (parsed.hours % 12 || 12) : 12;
  const ampm = parsed ? (parsed.hours >= 12 ? 'PM' : 'AM') : 'AM';
  const displayValue = value ? `${parsed?.day} ${months[parsed?.month - 1]} ${parsed?.year}, ${String(hours12).padStart(2, '0')}:${String(parsed?.minutes).padStart(2, '0')} ${ampm}` : 'Select date & time';

  return (
    <div className="relative">
      <label className="mb-2 block text-sm font-semibold text-slate-100">{label}</label>

      <div className="relative">
        <button
          type="button"
          onClick={() => setShowCalendar(!showCalendar)}
          className="w-full rounded-xl border border-white/10 bg-slate-900/70 px-4 py-3 text-sm text-white text-left transition hover:border-blue-300 focus:border-blue-400 focus:outline-none"
        >
          <div className="flex items-center gap-2">
            <Clock size={16} className="flex-shrink-0 text-slate-400" />
            <span>{displayValue}</span>
          </div>
        </button>

        {showCalendar && (
          <div className="absolute top-full left-0 z-50 mt-2 w-full rounded-xl border border-white/10 bg-slate-900 p-4 shadow-[0_20px_40px_rgba(0,0,0,0.3)]">
            {/* Quick Actions */}
            <div className="mb-4 flex gap-2">
              <button
                type="button"
                onClick={() => {
                  const now = new Date();
                  const newValue = formatLocalDatetimeValue(
                    now.getFullYear(),
                    now.getMonth() + 1,
                    now.getDate(),
                    now.getHours(),
                    now.getMinutes()
                  );
                  onChange({ target: { value: newValue } });
                  setDisplayMonth(now);
                }}
                className="flex-1 rounded-lg bg-blue-600 px-3 py-2 text-xs font-semibold text-white transition hover:bg-blue-700"
              >
                Now
              </button>
            </div>

            {/* Calendar Header */}
            <div className="mb-4 flex items-center justify-between">
              <button
                type="button"
                onClick={handlePrevMonth}
                className="rounded-lg p-1 text-slate-300 hover:bg-white/10 hover:text-white"
              >
                <ChevronLeft size={20} />
              </button>
              <h3 className="text-sm font-semibold text-white">
                {months[displayMonth.getMonth()]} {displayMonth.getFullYear()}
              </h3>
              <button
                type="button"
                onClick={handleNextMonth}
                className="rounded-lg p-1 text-slate-300 hover:bg-white/10 hover:text-white"
              >
                <ChevronRight size={20} />
              </button>
            </div>

            {/* Weekday Headers */}
            <div className="mb-2 grid grid-cols-7 gap-1">
              {weekDays.map((day) => (
                <div key={day} className="text-center text-xs font-semibold text-slate-400">
                  {day}
                </div>
              ))}
            </div>

            {/* Calendar Days */}
            <div className="mb-4 grid grid-cols-7 gap-1">
              {[...emptyDays, ...days].map((day, idx) => (
                <button
                  key={idx}
                  type="button"
                  onClick={() => day && !isDateDisabled(displayMonth.getFullYear(), displayMonth.getMonth() + 1, day) && handleDateClick(day)}
                  disabled={!day || isDateDisabled(displayMonth.getFullYear(), displayMonth.getMonth() + 1, day)}
                  className={`
                    rounded-lg py-2 text-xs font-medium transition
                    ${!day ? 'invisible' : ''}
                    ${isDateDisabled(displayMonth.getFullYear(), displayMonth.getMonth() + 1, day)
                      ? 'cursor-not-allowed text-slate-600'
                      : isDateSelected(displayMonth.getFullYear(), displayMonth.getMonth() + 1, day)
                        ? 'bg-blue-600 text-white shadow-[0_4px_12px_rgba(37,99,235,0.4)]'
                        : 'text-slate-200 hover:bg-white/10'
                    }
                  `}
                >
                  {day}
                </button>
              ))}
            </div>

            {/* Time Picker */}
            <div className="border-t border-white/10 pt-4">
              <p className="mb-3 text-xs font-semibold text-slate-300">Time (12-hour format)</p>
              <div className="flex items-center gap-2">
                <div className="flex-1">
                  <label className="block text-xs text-slate-400 mb-1">Hours</label>
                  <input
                    type="number"
                    min="1"
                    max="12"
                    value={parsed ? String((parsed.hours % 12 || 12)).padStart(2, '0') : '12'}
                    onChange={(e) => {
                      let hours = parseInt(e.target.value);
                      const ampmValue = parsed ? (parsed.hours >= 12 ? 'PM' : 'AM') : 'AM';
                      if (ampmValue === 'PM' && hours !== 12) hours += 12;
                      if (ampmValue === 'AM' && hours === 12) hours = 0;
                      handleTimeChange('hours', hours);
                    }}
                    className="w-full rounded-lg border border-white/10 bg-slate-800 px-3 py-2 text-center text-sm font-semibold text-white outline-none focus:border-blue-400"
                  />
                </div>
                <div className="flex items-center pt-6 text-white font-semibold">:</div>
                <div className="flex-1">
                  <label className="block text-xs text-slate-400 mb-1">Minutes</label>
                  <input
                    type="number"
                    min="0"
                    max="59"
                    value={String(parsed?.minutes ?? 0).padStart(2, '0')}
                    onChange={(e) => handleTimeChange('minutes', e.target.value)}
                    className="w-full rounded-lg border border-white/10 bg-slate-800 px-3 py-2 text-center text-sm font-semibold text-white outline-none focus:border-blue-400"
                  />
                </div>
                <div className="flex flex-col gap-1 pt-6">
                  <button
                    type="button"
                    onClick={() => {
                      const parsedVal = parseLocalDatetimeValue(value);
                      if (!parsedVal) {
                        const today = new Date();
                        const newValue = formatLocalDatetimeValue(today.getFullYear(), today.getMonth() + 1, today.getDate(), 9, 0);
                        onChange({ target: { value: newValue } });
                      } else {
                        let hours = parsedVal.hours;
                        if (hours < 12) {
                          hours += 12;
                        }
                        const newValue = formatLocalDatetimeValue(parsedVal.year, parsedVal.month, parsedVal.day, hours, parsedVal.minutes);
                        onChange({ target: { value: newValue } });
                      }
                    }}
                    className={`px-2 py-1 rounded-lg text-xs font-semibold transition ${
                      parsed && parsed.hours >= 12
                        ? 'bg-blue-600 text-white shadow-[0_4px_12px_rgba(37,99,235,0.4)]'
                        : 'text-slate-300 hover:bg-white/10'
                    }`}
                  >
                    PM
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      const parsedVal = parseLocalDatetimeValue(value);
                      if (!parsedVal) {
                        const today = new Date();
                        const newValue = formatLocalDatetimeValue(today.getFullYear(), today.getMonth() + 1, today.getDate(), 9, 0);
                        onChange({ target: { value: newValue } });
                      } else {
                        let hours = parsedVal.hours;
                        if (hours >= 12) {
                          hours -= 12;
                        }
                        const newValue = formatLocalDatetimeValue(parsedVal.year, parsedVal.month, parsedVal.day, hours, parsedVal.minutes);
                        onChange({ target: { value: newValue } });
                      }
                    }}
                    className={`px-2 py-1 rounded-lg text-xs font-semibold transition ${
                      parsed && parsed.hours < 12
                        ? 'bg-blue-600 text-white shadow-[0_4px_12px_rgba(37,99,235,0.4)]'
                        : 'text-slate-300 hover:bg-white/10'
                    }`}
                  >
                    AM
                  </button>
                </div>
              </div>
            </div>

            {/* Close Button */}
            <button
              type="button"
              onClick={() => setShowCalendar(false)}
              className="mt-4 w-full rounded-lg bg-blue-600 py-2 text-sm font-semibold text-white transition hover:bg-blue-700"
            >
              Done
            </button>
          </div>
        )}
      </div>

      {hint && (
        <p className="mt-2 text-xs leading-5 text-slate-300">
          {hint}
        </p>
      )}
    </div>
  );
};

export default DateTimePickerField;
