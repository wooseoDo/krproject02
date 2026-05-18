import type { ReactNode } from 'react';

export interface DataTableColumn<TItem> {
  id: string;
  header: string;
  cell: (item: TItem, index: number) => ReactNode;
  align?: 'left' | 'right' | 'center';
}

interface DataTableProps<TItem> {
  data: TItem[];
  columns: DataTableColumn<TItem>[];
  keyExtractor: (item: TItem) => string;
  emptyMessage: string;
}

function getAlignClassName(align: DataTableColumn<unknown>['align'] = 'center') {
  // Keep table content centered by default while allowing explicit overrides.
  return `is-${align}`;
}

export function DataTable<TItem>({ data, columns, keyExtractor, emptyMessage }: DataTableProps<TItem>) {
  return (
    <div className="data-table-wrap">
      <table className="data-table">
        <thead>
          <tr>
            {columns.map((column) => (
              <th className={getAlignClassName(column.align)} key={column.id}>
                {column.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.length === 0 ? (
            <tr>
              <td className="data-table__empty" colSpan={columns.length}>
                {emptyMessage}
              </td>
            </tr>
          ) : (
            data.map((item, index) => (
              <tr key={keyExtractor(item)}>
                {columns.map((column) => (
                  <td className={getAlignClassName(column.align)} key={column.id}>
                    {column.cell(item, index)}
                  </td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
