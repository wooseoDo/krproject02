import type { ChangeEvent } from 'react';

export interface SelectOption {
  value: string;
  label: string;
}

export type FilterField =
  | {
      id: string;
      label: string;
      type: 'text' | 'number' | 'date';
      value: string;
      placeholder?: string;
      onChange: (value: string) => void;
    }
  | {
      id: string;
      label: string;
      type: 'select';
      value: string;
      options: SelectOption[];
      onChange: (value: string) => void;
    };

interface ResponsiveSearchFilterProps {
  fields: FilterField[];
  onReset: () => void;
}

export function ResponsiveSearchFilter({ fields, onReset }: ResponsiveSearchFilterProps) {
  return (
    <section className="responsive-filter" aria-label="검색 필터">
      {fields.map((field) => (
        <label className="responsive-filter__field" key={field.id}>
          <span>{field.label}</span>
          {field.type === 'select' ? (
            <select
              value={field.value}
              onChange={(event: ChangeEvent<HTMLSelectElement>) => field.onChange(event.target.value)}
            >
              {field.options.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              type={field.type}
              value={field.value}
              placeholder={field.placeholder}
              onChange={(event: ChangeEvent<HTMLInputElement>) => field.onChange(event.target.value)}
            />
          )}
        </label>
      ))}
      <button className="filter-reset-button" type="button" onClick={onReset}>
        초기화
      </button>
    </section>
  );
}
