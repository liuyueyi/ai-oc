import { getGlobalConfig, GlobalConfigItem, GlobalConfigItemValue } from "./api";

const CACHE_KEY = "globalConfigCache";
const CACHE_DURATION_MS = 5 * 60 * 1000; // 5 minutes

interface ConfigCache {
    timestamp: number;
    data: { [key: string]: GlobalConfigItem };
}

async function getFullConfig(): Promise<{ [key: string]: GlobalConfigItem }> {
    if (typeof window === 'undefined') {
        return await getGlobalConfig();
    }

    const cachedItem = localStorage.getItem(CACHE_KEY);
    const now = Date.now();

    if (cachedItem) {
        try {
            const cache: ConfigCache = JSON.parse(cachedItem);
            if (now - cache.timestamp < CACHE_DURATION_MS) {
                return cache.data;
            }
        } catch (error) {
            console.error("Error parsing global config cache:", error);
        }
    }

    const configData = await getGlobalConfig();
    const newCache: ConfigCache = {
        timestamp: now,
        data: configData,
    };

    try {
        localStorage.setItem(CACHE_KEY, JSON.stringify(newCache));
    } catch (error) {
        console.error("Error saving global config to localStorage:", error);
    }

    return configData;
}

/**
 * Retrieves a specific application's configuration based on its key (appName).
 * The configuration is fetched from the API and cached in localStorage for 5 minutes.
 *
 * @param appName The key for the desired application configuration.
 * @returns A promise that resolves to the GlobalConfigItem, or undefined if not found.
 */
export async function getAppConfig(appName: string): Promise<GlobalConfigItem | undefined> {
    const fullConfig = await getFullConfig();
    return fullConfig[appName];
}

/**
 * Retrieves specific configuration values for a given application and item key.
 *
 * @param appName The name of the application.
 * @param itemKey The key of the configuration item.
 * @returns A promise that resolves to an array of configuration value strings.
 */
export async function getConfigValue(appName: string, itemKey: string): Promise<GlobalConfigItemValue[]> {
    const appConfig = await getAppConfig(appName);
    if (!appConfig || !appConfig.items) {
        return [];
    }

    const configItems = appConfig.items.filter(item => item.key === itemKey);
    return configItems;
} 