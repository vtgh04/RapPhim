import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import ConfirmModal from './ConfirmModal';

// Mock UI Store translation helper
vi.mock('../../store/uiStore', () => ({
  useUIStore: () => ({
    t: (key) => {
      const mockTranslations = {
        delete: 'Xóa',
        saveChanges: 'Lưu thay đổi',
        cancel: 'Hủy'
      };
      return mockTranslations[key] || key;
    }
  })
}));

// Mock framer-motion to simplify rendering and avoid animation timing issues in tests
vi.mock('framer-motion', () => ({
  motion: {
    div: ({ children, className, onClick, ...props }) => (
      <div className={className} onClick={onClick} {...props}>
        {children}
      </div>
    )
  },
  AnimatePresence: ({ children }) => <>{children}</>
}));

// Mock lucide-react icons
vi.mock('lucide-react', () => ({
  AlertTriangle: () => <span data-testid="alert-icon" />,
  X: () => <span data-testid="close-icon" />
}));

describe('ConfirmModal Component', () => {
  const defaultProps = {
    isOpen: true,
    onClose: vi.fn(),
    onConfirm: vi.fn(),
    title: 'Xác nhận xóa',
    message: 'Bạn có chắc chắn muốn xóa mục này không?',
    confirmText: 'Xóa ngay',
    cancelText: 'Bỏ qua',
    type: 'danger'
  };

  it('should not render anything when isOpen is false', () => {
    const { container } = render(<ConfirmModal {...defaultProps} isOpen={false} />);
    expect(container.firstChild).toBeNull();
  });

  it('should render correct title, message, and button texts when open', () => {
    render(<ConfirmModal {...defaultProps} />);
    
    expect(screen.getByText('Xác nhận xóa')).toBeInTheDocument();
    expect(screen.getByText('Bạn có chắc chắn muốn xóa mục này không?')).toBeInTheDocument();
    expect(screen.getByText('Xóa ngay')).toBeInTheDocument();
    expect(screen.getByText('Bỏ qua')).toBeInTheDocument();
    expect(screen.getByTestId('alert-icon')).toBeInTheDocument();
  });

  it('should call onClose when cancel button is clicked', () => {
    const onCloseMock = vi.fn();
    render(<ConfirmModal {...defaultProps} onClose={onCloseMock} />);
    
    const cancelButton = screen.getByText('Bỏ qua');
    fireEvent.click(cancelButton);
    expect(onCloseMock).toHaveBeenCalledTimes(1);
  });

  it('should call onConfirm and onClose when confirm button is clicked', () => {
    const onConfirmMock = vi.fn();
    const onCloseMock = vi.fn();
    render(<ConfirmModal {...defaultProps} onConfirm={onConfirmMock} onClose={onCloseMock} />);
    
    const confirmButton = screen.getByText('Xóa ngay');
    fireEvent.click(confirmButton);
    expect(onConfirmMock).toHaveBeenCalledTimes(1);
    expect(onCloseMock).toHaveBeenCalledTimes(1);
  });

  it('should fallback to default store translation values if texts are not provided', () => {
    render(
      <ConfirmModal
        isOpen={true}
        onClose={vi.fn()}
        onConfirm={vi.fn()}
        title="Default Title"
        message="Default Message"
        type="danger"
      />
    );
    
    // Default danger confirm text is t('delete') -> 'Xóa'
    expect(screen.getByText('Xóa')).toBeInTheDocument();
    // Default cancel text is t('cancel') -> 'Hủy'
    expect(screen.getByText('Hủy')).toBeInTheDocument();
  });
});
