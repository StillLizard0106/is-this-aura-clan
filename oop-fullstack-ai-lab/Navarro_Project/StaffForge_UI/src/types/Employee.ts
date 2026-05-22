/**
 * Employee Type Definition
 *
 * Vibe: "I define the contract between frontend and backend."
 * Enforces Encapsulation — all components use this contract
 */

export interface Employee {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  department: string;
  position: string;
}
