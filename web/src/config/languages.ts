import { SupportedLanguage } from '@/types/api';

export interface LanguageConfig {
  code: string;
  name: string;
  nativeName: string;
  direction: 'ltr' | 'rtl';
  isRtl: boolean;
  enumKey: SupportedLanguage;
}

export const SUPPORTED_LANGUAGES: Record<string, LanguageConfig> = {
  fr: {
    code: 'fr',
    name: 'Français',
    nativeName: 'Français',
    direction: 'ltr',
    isRtl: false,
    enumKey: 'FRENCH',
  },
  ar: {
    code: 'ar',
    name: 'Arabic',
    nativeName: 'العربية',
    direction: 'rtl',
    isRtl: true,
    enumKey: 'ARABIC',
  },
  tn: {
    code: 'tn',
    name: 'Tunisian Derja',
    nativeName: 'تونسي دارجة',
    direction: 'rtl',
    isRtl: true,
    enumKey: 'DERJA',
  },
  en: {
    code: 'en',
    name: 'English',
    nativeName: 'English',
    direction: 'ltr',
    isRtl: false,
    enumKey: 'ENGLISH',
  },
};

export const DEFAULT_LANGUAGE = SUPPORTED_LANGUAGES.fr;
