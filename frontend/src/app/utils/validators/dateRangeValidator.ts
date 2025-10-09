import { AbstractControl, ValidationErrors } from '@angular/forms';

export function validateDateRange(
  control: AbstractControl,
): ValidationErrors | null {
  const value = control.value;

  if (!Array.isArray(value) || value.length !== 2) {
    return { invalidDateRange: 'Must be an array of two dates' };
  }

  const [start, end] = value;

  if (
    !(start instanceof Date) ||
    isNaN(start.getTime()) ||
    !(end instanceof Date) ||
    isNaN(end.getTime())
  ) {
    return { invalidDateRange: 'Invalid date objects' };
  }

  if (start >= end) {
    return { invalidDateRange: 'Start date must be before end date' };
  }

  return null;
}
