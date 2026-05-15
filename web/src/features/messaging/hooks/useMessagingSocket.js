import { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { WS_BASE_URL, ROLES } from '../../../shared/utils/constants';

const normalizeRole = (role) => {
  if (role === 'registered_user' || role === ROLES.USER) {
    return ROLES.REGISTERED_USER;
  }
  return role;
};

export function useMessagingSocket({ user, role, conversationId, onInboxEvent, onLockedEvent, onMessage }) {
  const clientRef = useRef(null);
  const [connected, setConnected] = useState(false);
  const normalizedRole = normalizeRole(role || user?.role);

  useEffect(() => {
    if (!user?.id || !normalizedRole) {
      return undefined;
    }

    const token = localStorage.getItem('accessToken');
    const client = new Client({
      webSocketFactory: () => new SockJS(WS_BASE_URL),
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      reconnectDelay: 5000,
      debug: () => {},
      onConnect: () => {
        setConnected(true);

        if (normalizedRole === ROLES.AGENT) {
          client.subscribe('/topic/agents/inbox', (message) => {
            onInboxEvent?.(JSON.parse(message.body));
          });

          if (conversationId) {
            client.subscribe(`/topic/agents/conversations/${conversationId}/locked`, (message) => {
              onLockedEvent?.(JSON.parse(message.body));
            });
          }
        }

        client.subscribe('/user/queue/messages', (message) => {
          onMessage?.(JSON.parse(message.body));
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
  }, [conversationId, normalizedRole, onInboxEvent, onLockedEvent, onMessage, user?.id]);

  return { connected };
}

export default useMessagingSocket;
