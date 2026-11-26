import { describe, expect, it, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Button from './Button';

describe('<Button />', () => {
  it('renders children and handles clicks', async () => {
    const onClick = vi.fn();
    render(<Button onClick={onClick}>Save</Button>);

    await userEvent.click(screen.getByRole('button', { name: 'Save' }));

    expect(onClick).toHaveBeenCalledOnce();
  });

  it('is disabled and shows a loading label while loading', async () => {
    const onClick = vi.fn();
    render(
      <Button loading onClick={onClick}>
        Save
      </Button>,
    );

    const button = screen.getByRole('button');
    expect(button).toBeDisabled();
    expect(button).toHaveTextContent('Loading...');

    await userEvent.click(button);
    expect(onClick).not.toHaveBeenCalled();
  });

  it('falls back to the primary variant for unknown variants', () => {
    render(<Button variant="does-not-exist">Go</Button>);
    expect(screen.getByRole('button')).toHaveClass('bg-blue-600');
  });
});
