import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

// Giống chính sách backend: 8-72 ký tự, có ít nhất 1 chữ cái và 1 chữ số
export const PASSWORD_PATTERN = /^(?=.*[A-Za-z])(?=.*\d).{8,72}$/;
export const PASSWORD_HINT = 'Mật khẩu 8-72 ký tự, gồm ít nhất 1 chữ cái và 1 chữ số';

export function matchFields(a: string, b: string): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null =>
    group.get(a)?.value === group.get(b)?.value ? null : { mismatch: true };
}
