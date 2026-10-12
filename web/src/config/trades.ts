import { Trade, TradeId } from '@/types/api';

export const SUPPORTED_TRADES: Record<TradeId, Trade> = {
  placo: {
    id: 'placo',
    localizationKey: 'trade_placo',
    iconEmoji: '🏗️',
    workTypeCount: 7,
    supportedUnitSystems: ['METRIC'],
  },
  peinture: {
    id: 'peinture',
    localizationKey: 'trade_peinture',
    iconEmoji: '🎨',
    workTypeCount: 2,
    supportedUnitSystems: ['METRIC'],
  },
  maconnerie: {
    id: 'maconnerie',
    localizationKey: 'trade_maconnerie',
    iconEmoji: '🧱',
    workTypeCount: 1,
    supportedUnitSystems: ['METRIC'],
  },
  crepissage: {
    id: 'crepissage',
    localizationKey: 'trade_crepissage',
    iconEmoji: '🖌️',
    workTypeCount: 1,
    supportedUnitSystems: ['METRIC'],
  },
  carrelage: {
    id: 'carrelage',
    localizationKey: 'trade_carrelage',
    iconEmoji: '🟦',
    workTypeCount: 1,
    supportedUnitSystems: ['METRIC'],
  },
  menuiserie: {
    id: 'menuiserie',
    localizationKey: 'trade_menuiserie',
    iconEmoji: '🪟',
    workTypeCount: 1,
    supportedUnitSystems: ['METRIC'],
  },
};
