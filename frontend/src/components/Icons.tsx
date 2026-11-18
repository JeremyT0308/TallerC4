import type { SVGProps } from 'react';

export type IconName = 'arrow' | 'car' | 'check' | 'chevron-down' | 'chevron-up' | 'close' | 'mail' | 'person' | 'shield' | 'spark' | 'spinner' | 'warning';

const paths: Record<IconName, string> = {
  arrow: 'M5 12h14M13 6l6 6-6 6',
  car: 'M5 17h14l-1-6H6l-1 6Zm2-6 1.5-4h7L17 11M8 17v2m8-2v2M7 14h.01M17 14h.01',
  check: 'm5 12 4 4L19 6',
  'chevron-down': 'm6 9 6 6 6-6',
  'chevron-up': 'm6 15 6-6 6 6',
  close: 'm6 6 12 12M18 6 6 18',
  mail: 'M4 6h16v12H4zM4 7l8 6 8-6',
  person: 'M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm-7 8a7 7 0 0 1 14 0',
  shield: 'M12 3 19 6v5c0 4.5-2.9 8-7 10-4.1-2-7-5.5-7-10V6l7-3Zm-3 9 2 2 4-4',
  spark: 'm12 2 1.6 6.4L20 10l-6.4 1.6L12 18l-1.6-6.4L4 10l6.4-1.6L12 2Zm6 13 .7 2.3L21 18l-2.3.7L18 21l-.7-2.3L15 18l2.3-.7L18 15Z',
  spinner: 'M12 3a9 9 0 1 0 9 9',
  warning: 'M12 4 21 19H3L12 4Zm0 5v4m0 3h.01',
};

export function Icon({ name, size = 18, ...props }: { name: IconName; size?: number } & SVGProps<SVGSVGElement>) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true" {...props}>
      <path d={paths[name]} />
    </svg>
  );
}
