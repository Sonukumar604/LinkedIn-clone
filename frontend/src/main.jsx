import { StrictMode, createContext, useContext, useEffect, useState } from 'react'
import { createRoot } from 'react-dom/client'
import { Bell, BriefcaseBusiness, ChevronDown, ImagePlus, LogOut, Send, UserRound, UsersRound } from 'lucide-react'
import { connectionsApi, notificationsApi, postsApi, usersApi } from './api'
import './styles.css'

const AuthContext = createContext(null)

function getUserIdFromToken(token) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    return Number(payload.userId || payload.id || payload.sub) || 1
  } catch {
    return 1
  }
}

function AuthProvider({ children }) {
  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem('linkedin_user') || 'null'))
  const login = async (credentials) => {
    const { data: token } = await usersApi.login(credentials)
    const nextUser = { name: credentials.email.split('@')[0], email: credentials.email, id: getUserIdFromToken(token) }
    localStorage.setItem('linkedin_token', token)
    localStorage.setItem('linkedin_user', JSON.stringify(nextUser))
    setUser(nextUser)
  }
  const logout = () => { localStorage.clear(); setUser(null) }
  return <AuthContext.Provider value={{ user, login, logout }}>{children}</AuthContext.Provider>
}

const useAuth = () => useContext(AuthContext)

function LoginPanel() {
  const { login } = useAuth()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const submit = async (event) => {
    event.preventDefault(); setError('')
    try { await login(form) } catch { setError('Could not sign in. Check your details and try again.') }
  }
  return <main className="login-shell"><section className="login-card">
    <div className="brand-mark">in<span>.</span></div><p className="eyebrow">Your professional circle</p>
    <h1>Make your next move meaningful.</h1><p className="muted">Sign in to see what your network is building.</p>
    <form onSubmit={submit} className="stack">
      <label>Email<input required type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label>
      <label>Password<input required type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /></label>
      {error && <p className="error">{error}</p>}<button className="primary" type="submit">Sign in <Send size={16} /></button>
    </form>
  </section></main>
}

function Header() {
  const { user, logout } = useAuth()
  return <header className="topbar"><div className="topbar-inner"><div className="brand-mark small">in<span>.</span></div><div className="search">Search your network</div><nav><a className="active"><BriefcaseBusiness size={18} />My network</a><a><Bell size={18} />Notifications</a><button className="profile-button" onClick={logout}><UserRound size={18} />{user.name}<ChevronDown size={15} /></button></nav></div></header>
}

function ProfileSidebar({ connections }) {
  const { user } = useAuth()
  return <aside className="sidebar"><div className="profile-card"><div className="cover" /><div className="avatar large">{user.name[0]?.toUpperCase()}</div><h2>{user.name}</h2><p className="muted">Product-minded builder · Open to opportunities</p><div className="profile-stats"><span>Profile views <b>48</b></span><span>Connections <b>{connections}</b></span></div></div><div className="side-note"><UsersRound size={18} /><div><strong>Grow your circle</strong><p className="muted">Connect with people who help you think bigger.</p></div></div></aside>
}

function Composer({ onCreated }) {
  const [content, setContent] = useState(''); const [sending, setSending] = useState(false)
  const submit = async (event) => { event.preventDefault(); if (!content.trim()) return; setSending(true); try { await postsApi.create(content); setContent(''); onCreated() } finally { setSending(false) } }
  return <form className="composer" onSubmit={submit}><div className="avatar">Y</div><textarea value={content} onChange={(e) => setContent(e.target.value)} placeholder="Share an idea, a win, or a question..." rows="3" /><div className="composer-actions"><button type="button" className="icon-button" title="Add an image"><ImagePlus size={19} />Media</button><button className="primary compact" disabled={sending}>{sending ? 'Sharing...' : 'Share'} <Send size={15} /></button></div></form>
}

function Post({ post }) { return <article className="post"><div className="post-heading"><div className="avatar">{String(post.userId || 'N')[0]}</div><div><strong>Network member</strong><p className="muted">Shared a thought · {post.createdAt ? new Date(post.createdAt).toLocaleDateString() : 'just now'}</p></div></div><p className="post-content">{post.content}</p><div className="post-footer"><button>Like</button><button>Comment</button><button>Share</button></div></article> }

function Feed() {
  const { user } = useAuth(); const [posts, setPosts] = useState([]); const [connections, setConnections] = useState(0); const [noticeCount, setNoticeCount] = useState(0)
  const load = async () => { try { const [postResponse, connectionResponse, noticeResponse] = await Promise.all([postsApi.byUser(user.id), connectionsApi.firstDegree(user.id), notificationsApi.list(user.id).catch(() => ({ data: [] }))]); setPosts(postResponse.data || []); setConnections(connectionResponse.data?.length || 0); setNoticeCount(noticeResponse.data?.length || 0) } catch { setPosts([]) } }
  useEffect(() => { load() }, [])
  return <><Header /><main className="layout"><ProfileSidebar connections={connections} /><section className="feed"><div className="feed-intro"><div><p className="eyebrow">Thursday, September 04</p><h1>Your network, in motion.</h1></div><span className="notification-pill"><Bell size={15} />{noticeCount} new</span></div><Composer onCreated={load} /><div className="section-label"><span>Latest from your circle</span><span className="muted">Sort: Recent <ChevronDown size={14} /></span></div>{posts.length ? posts.map((post) => <Post key={post.id} post={post} />) : <div className="empty"><div className="empty-icon"><UsersRound /></div><h2>Your feed is ready for its first post.</h2><p className="muted">Share something with your network to start the conversation.</p></div>}</section></main></>
}

function App() { return <AuthProvider>{useAuth ? <AuthGate /> : null}</AuthProvider> }
function AuthGate() { const { user } = useAuth(); return user ? <Feed /> : <LoginPanel /> }
createRoot(document.getElementById('root')).render(<StrictMode><App /></StrictMode>)