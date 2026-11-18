import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import App from './App';

describe('TallerC4 app shell', () => {
  it('shows the Spanish query heading', () => {
    render(<App />);
    expect(screen.getByRole('heading', { name: /tallerc4/i })).toBeInTheDocument();
  });
});
