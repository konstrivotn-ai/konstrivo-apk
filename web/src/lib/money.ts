import { Money } from '@/types/api';

/**
 * Creates a valid Money value object.
 */
export function createMoney(amount: number, currency = 'TND'): Money {
  if (isNaN(amount) || !isFinite(amount)) {
    return { amount: 0, currency };
  }
  return {
    amount: Math.round(amount * 1000) / 1000,
    currency: currency.toUpperCase(),
  };
}

/**
 * Formats a Money object for localized user display only.
 * NOTE: Formatted strings are for UI presentation only, NEVER serialized to API.
 */
export function formatMoney(
  money: Money | undefined | null,
  locale = 'fr-TN',
  decimals = 3
): string {
  if (!money) return '0.000';
  
  const formattedAmount = new Intl.NumberFormat(locale, {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals,
  }).format(money.amount);

  return `${formattedAmount} ${money.currency}`;
}

/**
 * Sums an array of Money objects with currency validation.
 */
export function sumMoney(items: Money[], targetCurrency?: string): Money {
  if (!items.length) {
    return { amount: 0, currency: targetCurrency || 'TND' };
  }

  const currency = targetCurrency || items[0].currency;
  const total = items.reduce((acc, curr) => {
    if (curr.currency !== currency) {
      console.warn(`Currency mismatch in sum: ${curr.currency} vs ${currency}`);
    }
    return acc + (curr.amount || 0);
  }, 0);

  return createMoney(total, currency);
}
