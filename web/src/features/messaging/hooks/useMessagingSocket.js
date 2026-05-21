import { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { WS_BASE_URL, ROLES } from '../../../shared/utils/constants';

const normalizeRole = (role) => {
  if (role === 'registered_user' || role === ROLES.USER) {
    return ROLES.REGISTERED_USER;
  }
  return role?.toUpperCase?.() || role;
};

export function useMessagingSocket({ user, role, conversationId, onInboxEvent, onLockedEvent, onMessage, enabled = true }) {
  const clientRef = useRef(null);
  const handlersRef = useRef({ onInboxEvent, onLockedEvent, onMessage });
  const [connected, setConnected] = useState(false);
  const normalizedRole = normalizeRole(role || user?.role || localStorage.getItem('role'));

  useEffect(() => {
    // Keep callbacks fresh without forcing a WebSocket reconnect on every render.
    handlersRef.current = { onInboxEvent, onLockedEvent, onMessage };
  }, [onInboxEvent, onLockedEvent, onMessage]);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (!enabled || !token || !normalizedRole) {
      return undefined;
    }

    const client = new Client({
      webSocketFactory: () => new SockJS(WS_BASE_URL),
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,
      debug: () => {},
      onConnect: () => {
        setConnected(true);

        if (normalizedRole === ROLES.AGENT) {
          client.subscribe('/topic/agents/inbox', (message) => {
            handlersRef.current.onInboxEvent?.(JSON.parse(message.body));
          });

          if (conversationId) {
            client.subscribe(`/topic/agents/conversations/${conversationId}/locked`, (message) => {
              handlersRef.current.onLockedEvent?.(JSON.parse(message.body));
            });
            client.subscribe(`/topic/agents/conversations/${conversationId}/messages`, (message) => {
              handlersRef.current.onMessage?.(JSON.parse(message.body));
            });
          }
        }

        // User destinations deliver private replies for assigned conversations.
        client.subscribe('/user/queue/messages', (message) => {
          handlersRef.current.onMessage?.(JSON.parse(message.body));
        });
      },
      onDisconnect: () => setConnected(false),
      onStompError: () => setConnected(false),
      onWebSocketClose: () => setConnected(false),
    });

    clientRef.current = client;
    client.activate();

    return () => {
      client.deactivate();
      clientRef.current = null;
      setConnected(false);
    };
  }, [conversationId, enabled, normalizedRole]);

  return { connected };
}

export default useMessagingSocket;
